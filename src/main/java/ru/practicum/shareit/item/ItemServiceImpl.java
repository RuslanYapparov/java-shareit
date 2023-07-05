package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.booking.dao.BookingEntity;
import ru.practicum.shareit.common.CrudServiceImpl;
import ru.practicum.shareit.common.ShareItConstants;
import ru.practicum.shareit.exception.BadRequestBodyException;
import ru.practicum.shareit.exception.BadRequestHeaderException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dao.CommentEntity;
import ru.practicum.shareit.item.comment.dao.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentRestCommand;
import ru.practicum.shareit.item.dao.ItemEntity;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemRestCommand;
import ru.practicum.shareit.item.dto.ItemRestView;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dao.UserShort;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl extends CrudServiceImpl<ItemEntity, Item, ItemRestCommand, ItemRestView> implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           ItemValidator itemValidator,
                           ItemMapper itemMapper,
                           CommentRepository commentRepository) {
        this.entityRepository = itemRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.domainObjectValidator = itemValidator;
        this.objectMapper = itemMapper;
        this.commentRepository = commentRepository;
        this.type = "item";
    }

    @Override
    public ItemRestView save(long userId, ItemRestCommand itemRestCommand) {
        checkUserExistingAndReturnUserShort(userId);
        itemRestCommand = itemRestCommand.toBuilder()
                .ownerId(userId)
                .build();
        return super.save(userId, itemRestCommand);
    }

    @Override
    public List<ItemRestView> getAll(long userId) {
        if (userId == 0) {
            return super.getAll(userId);
        } else {
            checkUserExistingAndReturnUserShort(userId);
            List<ItemEntity> itemEntities = itemRepository.findAllByUserId(userId);
            List<Item> items = itemEntities.stream()
                    .map(this::mapItemWithLastAndNextBookings)
                    .collect(Collectors.toList());
            log.info("Запрошен список всех сохраненных объектов '{}', принадлежащих пользователю с id'{}'. " +
                    "Количество сохраненных объектов - {}", type, userId, itemEntities.size());
            return objectsToRestViewsListTransducer.apply(items);
        }
    }

    @Override
    public ItemRestView getById(long userId, long itemId) {
        ItemEntity itemEntity = checkUserAndObjectExistingAndReturnEntityFromDb(userId, itemId);
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
        ItemEntity itemEntity = checkUserAndObjectExistingAndReturnEntityFromDb(userId, itemId);
        String savedName = itemEntity.getName();
        String savedDescription = itemEntity.getDescription();
        boolean savedAvailable = itemEntity.isAvailable();

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
        Item item = objectMapper.fromDbEntity(entityRepository.save(itemEntity));
        log.info("Пользователь с идентификатором id{} обновил данные объекта '{}' с идентификатором id{}",
                userId, type, itemId);
        return objectMapper.toRestView(item);
    }

    @Override
    public void deleteAll(long userId) {
        if (userId == 0) {
            super.deleteAll(userId);
        } else {
            checkUserExistingAndReturnUserShort(userId);
            itemRepository.deleteAllByUserId(userId);
            log.info("Удалены все объекты '{}' из хранилища, связанные с пользователем с id'{}'", type, userId);
        }
    }

    @Override
    public List<ItemRestView> searchInNamesAndDescriptionsByText(long userId, String text) {
        checkUserExistingAndReturnUserShort(userId);
        List<ItemEntity> availableItemEntities = itemRepository
        .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByIdAsc(text, text).stream()
                .filter(ItemEntity::isAvailable)
                .collect(Collectors.toList());
        log.info("Произведен поиск всех вещей у всех пользователей, в названии и/или описании которых " +
                "присутствует текст '{}'. Найдено всего {} таких вещей", text, availableItemEntities.size());
        return objectsToRestViewsListTransducer.apply(entitiesToObjectsListTransducer.apply(availableItemEntities));
    }

    @Override
    public Comment addCommentToItem(long authorId, long itemId, CommentRestCommand commentRestCommand) {
        UserShort author = checkUserExistingAndReturnUserShort(authorId);
        String authorName = author.getName();
        ItemEntity itemEntity = entityRepository.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("В ходе выполнения операции над объектом '%s' с " +
                        "идентификатором id%d произошла ошибка: объект ранее не был сохранен", type, itemId)));
        boolean canUserComment = itemEntity.getItemBookings().stream()
                .filter(booking -> "APPROVED".equals(booking.getStatus()))
                .filter(booking -> authorId == booking.getId())
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
        log.info("Пользователь с идентификатором id{} добавил комментарий {} для вещи {} с идентификатором id{}",
                authorId, commentText, itemEntity.getName(), itemId);
        return new Comment(commentEntity.getId(), authorId, authorName, commentText,
                commentEntity.getCreated(), commentEntity.getLastModified());
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