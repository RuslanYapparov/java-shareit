package ru.practicum.shareit_server.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ru.practicum.shareit_server.booking.dao.BookingEntity;
import ru.practicum.shareit_server.common.CrudServiceImpl;
import ru.practicum.shareit_server.common.ShareItConstants;
import ru.practicum.shareit_server.exception.BadRequestBodyException;
import ru.practicum.shareit_server.exception.BadRequestHeaderException;
import ru.practicum.shareit_server.exception.ObjectNotFoundException;
import ru.practicum.shareit_server.item.comment.Comment;
import ru.practicum.shareit_server.item.comment.dao.CommentEntity;
import ru.practicum.shareit_server.item.comment.dao.CommentRepository;
import ru.practicum.shareit_server.item.comment.dto.CommentRestCommand;
import ru.practicum.shareit_server.item.dao.ItemEntity;
import ru.practicum.shareit_server.item.dao.ItemRepository;
import ru.practicum.shareit_server.item.dto.ItemRestCommand;
import ru.practicum.shareit_server.item.dto.ItemRestView;
import ru.practicum.shareit_server.request.dao.ItemRequestEntity;
import ru.practicum.shareit_server.request.dao.ItemRequestRepository;
import ru.practicum.shareit_server.user.dao.UserRepository;
import ru.practicum.shareit_server.user.dao.UserShort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl extends CrudServiceImpl<ItemEntity, Item, ItemRestCommand, ItemRestView> implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           CommentRepository commentRepository,
                           ItemRequestRepository itemRequestRepository,
                           ItemValidator itemValidator,
                           ItemMapper itemMapper) {
        this.entityRepository = itemRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.domainObjectValidator = itemValidator;
        this.objectMapper = itemMapper;
        this.type = "item";
    }

    @Override
    public ItemRestView save(long userId, ItemRestCommand itemRestCommand) {
        checkExistingAndReturnUserShort(userId);
        Item item = objectMapper.fromRestCommand(itemRestCommand);
        item = item.toBuilder()
                .ownerId(userId)
                .build();
        item = domainObjectValidator.validateAndAssignNullFields(item);
        ItemEntity itemEntity = objectMapper.toDbEntity(item);
        if (item.getRequestId() == 0L) {
            itemEntity = entityRepository.save(itemEntity);
            item = objectMapper.fromDbEntity(itemEntity);
            log.info("Пользователь с идентификатором id{} сохранил новый объект типа '{}'. Присвоен идентификатор id{}",
                    userId, type, item.getId());
            return objectMapper.toRestView(item);
        }
        ItemRequestEntity itemRequestEntity = itemRequestRepository.findById(item.getRequestId()).orElseThrow(() ->
                new BadRequestBodyException("Указанный в теле http-запроса идентификатор программного запроса " +
                        "на вещь не соответствует ни одному из сохраненных ранее"));
        itemEntity.setRequest(itemRequestEntity);
        itemEntity = entityRepository.save(itemEntity);
        List<ItemEntity> itemsInRequest = itemRequestEntity.getItems();
        if (itemsInRequest == null) {
            itemRequestEntity.setItems(new ArrayList<>(List.of(itemEntity)));
        } else {
            itemsInRequest.add(itemEntity);
            itemRequestEntity.setItems(itemsInRequest);
        }
        itemRequestRepository.save(itemRequestEntity);
        item = objectMapper.fromDbEntity(itemEntity);
        log.info("Пользователь с идентификатором id{} сохранил новый объект типа '{}'. Присвоен идентификатор id{}",
                userId, type, item.getId());
        return objectMapper.toRestView(item);
    }

    @Override
    public Page<ItemRestView> getAll(long userId, int from, int size) {
        checkExistingAndReturnUserShort(userId);
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(from / size, size, sortById);
        Page<ItemEntity> itemEntities = itemRepository.findAllByUserId(userId, page);
        Page<Item> items = itemEntities.map(this::mapItemWithLastAndNextBookings);
        log.info("Запрошен постраничный список всех сохраненных объектов '{}', принадлежащих пользователю " +
                "с id'{}'. Количество объектов для отображения на странице - {}, объекты отображаются " +
                "по порядку с {} по {}", type, userId, size, from + 1, items.getTotalElements());
        return items.map(objectMapper::toRestView);
    }

    @Override
    public ItemRestView getById(long userId, long itemId) {
        checkExistingAndReturnUserShort(userId);
        ItemEntity itemEntity = checkExistingAndReturnEntity(itemId);
        Item item;
        if (userId == itemEntity.getUserId()) {
            item = mapItemWithLastAndNextBookings(itemEntity);
        } else {
            item = objectMapper.fromDbEntity(itemEntity);
        }
        log.info("Пользователь с идентификатором id{} запросил данные объекта '{}' с идентификатором id{}",
                userId, type, itemId);
        return objectMapper.toRestView(item);
    }

    @Override
    public ItemRestView update(long userId, long itemId, ItemRestCommand itemCommand) {
        checkExistingAndReturnUserShort(userId);
        ItemEntity itemEntity = checkExistingAndReturnEntity(itemId);
        String savedName = itemEntity.getName();
        String savedDescription = itemEntity.getDescription();
        boolean savedAvailable = itemEntity.isAvailable();
        LocalDateTime created = itemEntity.getCreated();

        String updatedName = itemCommand.getName();
        String updatedDescription = itemCommand.getDescription();
        if (ShareItConstants.NOT_ASSIGNED.equals(updatedName) ||
                ShareItConstants.NOT_ASSIGNED.equals(updatedDescription)) {
            throw new BadRequestBodyException("В имени или описании вещи используется зарезервированное системой " +
                    "значение. Пожалуйста, выберите занчение, отличное от 'N/A'");
        }
        itemEntity.setName((updatedName == null) ? savedName : updatedName);
        itemEntity.setDescription((updatedDescription == null) ? savedDescription : updatedDescription);
        itemEntity.setAvailable((itemCommand.getAvailable() == null) ? savedAvailable : itemCommand.getAvailable());
        itemEntity.setCreated(created);
        itemEntity.setLastModified(LocalDateTime.now());
        Item item = objectMapper.fromDbEntity(entityRepository.save(itemEntity));
        log.info("Пользователь с идентификатором id{} обновил данные объекта '{}' с идентификатором id{}",
                userId, type, itemId);
        return objectMapper.toRestView(item);
    }

    @Override
    public void deleteAll(long userId) {
        checkExistingAndReturnUserShort(userId);
        itemRepository.deleteAllByUserId(userId);
        log.info("Удалены все объекты '{}' из хранилища, связанные с пользователем с id'{}'", type, userId);
    }

    @Override
    public Page<ItemRestView> searchInNamesAndDescriptionsByText(long userId, String text, int from, int size) {
        checkExistingAndReturnUserShort(userId);
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(from / size, size, sortById);
        Page<ItemEntity> availableItemEntities = itemRepository
                .findAllAvailableBySearchInNamesAndDescriptions(text, page);
        Page<Item> items = availableItemEntities.map(objectMapper::fromDbEntity);
        log.info("Произведен поиск всех вещей у всех пользователей, в названии и/или описании которых " +
                "присутствует текст '{}'. Количество объектов для отображения на странице - {}, объекты отображаются " +
                "по порядку с {} по {}", text, size, from + 1, items.getTotalElements());
        return items.map(objectMapper::toRestView);
    }

    @Override
    public Comment addCommentToItem(long authorId, long itemId, CommentRestCommand commentRestCommand) {
        UserShort author = checkExistingAndReturnUserShort(authorId);
        String authorName = author.getName();
        ItemEntity itemEntity = entityRepository.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("В ходе выполнения операции над объектом '%s' с " +
                        "идентификатором id%d произошла ошибка: объект ранее не был сохранен", type, itemId)));
        boolean canUserComment = itemEntity.getItemBookings().stream()
                .filter(booking -> "APPROVED".equals(booking.getStatus()))
                .filter(booking -> authorId == booking.getUserId())
                .anyMatch(booking -> booking.getStart().isBefore(LocalDateTime.now()));
        if (!canUserComment) {
            throw new BadRequestHeaderException(String.format("Пользователь %s с идентификатором id%d пытался " +
                    "оставить комментарий к вещи с идентификатором id%d, но операция была прервана, поскольку " +
                    "пользователь не пользовался данной вещью", authorName, authorId, itemId));
        }
        String commentText = commentRestCommand.getText();
        if (ShareItConstants.NOT_ASSIGNED.equals(commentText)) {
            throw new BadRequestBodyException("Текст отзыва не может быть сохранен, поскольку данная комбинация " +
                    "символов зарезервирована системой. Пожалуйста, выберите занчение, отличное от 'N/A'");
        }
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setUserId(authorId);
        commentEntity.setAuthorName(authorName);
        commentEntity.setItem(itemEntity);
        commentEntity.setText(commentText);
        commentEntity = commentRepository.save(commentEntity);
        List<CommentEntity> comments = itemEntity.getComments();
        if (comments == null || comments.isEmpty()) {
            comments = new ArrayList<>(List.of(commentEntity));
        } else {
            comments.add(commentEntity);
        }
        itemEntity.setComments(comments);
        entityRepository.save(itemEntity);
        log.info("Пользователь с идентификатором id{} добавил комментарий {} для вещи {} с идентификатором id{}",
                authorId, commentText, itemEntity.getName(), itemId);
        return Comment.builder().id(commentEntity.getId()).authorId(authorId).authorName(authorName).text(commentText)
                .created(commentEntity.getCreated()).lastModified(commentEntity.getLastModified()).build();
    }

    private Item mapItemWithLastAndNextBookings(ItemEntity itemEntity) {
        Item item = objectMapper.fromDbEntity(itemEntity);
        List<BookingEntity> itemBookings = itemEntity.getItemBookings();
        if (itemBookings == null || itemBookings.isEmpty()) {
            return item;
        }
        ItemBooking nextItemBooking = itemBookings.stream()
                .filter(bookingEntity -> "APPROVED".equals(bookingEntity.getStatus()))
                .filter(bookingEntity -> bookingEntity.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(BookingEntity::getStart))
                .map(bookingEntity -> new ItemBooking(bookingEntity.getId(), bookingEntity.getUserId(),
                        bookingEntity.getStatus(), bookingEntity.getStart(), bookingEntity.getEnd()))
                .orElse(null);
        ItemBooking lastItemBooking = itemBookings.stream()
                .filter(bookingEntity -> "APPROVED".equals(bookingEntity.getStatus()))
                .filter(bookingEntity -> bookingEntity.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(BookingEntity::getEnd))
                .map(bookingEntity -> new ItemBooking(bookingEntity.getId(), bookingEntity.getUserId(),
                        bookingEntity.getStatus(), bookingEntity.getStart(), bookingEntity.getEnd()))
                .orElse(null);
        return item.toBuilder()
                .nextBooking(nextItemBooking)
                .lastBooking(lastItemBooking)
                .build();
    }

}