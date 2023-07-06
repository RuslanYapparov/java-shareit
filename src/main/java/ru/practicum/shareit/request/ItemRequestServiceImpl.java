package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.common.CrudServiceImpl;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.dao.ItemRequestEntity;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestRestCommand;
import ru.practicum.shareit.request.dto.ItemRequestRestView;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.List;

@Service
@Slf4j
public class ItemRequestServiceImpl
        extends CrudServiceImpl<ItemRequestEntity, ItemRequest, ItemRequestRestCommand, ItemRequestRestView>
        implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  UserRepository userRepository,
                                  ItemRepository itemRepository,
                                  ItemRequestMapper itemRequestMapper
                                  ) {
        this.entityRepository = itemRequestRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.objectMapper = itemRequestMapper;
        this.type = "request";
    }

    @Override
    public ItemRequestRestView save(long userId, ItemRequestRestCommand itemRequestRestCommand) {
        checkUserExistingAndReturnUserShort(userId);
        ItemRequest itemRequest = objectMapper.fromRestCommand(itemRequestRestCommand);
        itemRequest = itemRequest.toBuilder()
                .requesterId(userId)
                .build();
        ItemRequestEntity itemRequestEntity = entityRepository.save(objectMapper.toDbEntity(itemRequest));
        itemRequest = objectMapper.fromDbEntity(itemRequestEntity);
        return objectMapper.toRestView(itemRequest);
    }

    @Override
    public List<ItemRequestRestView> getAllRequestsOfRequester(long requesterId) {
        checkUserExistingAndReturnUserShort(requesterId);
        List<ItemRequestEntity> itemRequestEntities =
                itemRequestRepository.findAllByUserIdOrderByCreatedDesc(requesterId);
        return objectsToRestViewsListTransducer.apply(entitiesToObjectsListTransducer.apply(itemRequestEntities));
    }

    @Override
    public Page<ItemRequestRestView> getAll(long userId, int from, int size) {
        Sort sortByCreatedDate = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(from, size, sortByCreatedDate);
        Page<ItemRequestEntity> itemRequestEntityPage = itemRequestRepository.findAllWithoutUsersRequests(userId, page);
        Page<ItemRequest> objectPage = itemRequestEntityPage.map(objectMapper::fromDbEntity);
        log.info("Запрошен постраничный список всех сохраненных объектов '{}'. Количество объектов для отображения " +
                "на странице - {}, объекты отображаются " +
                "по порядку с {} по {}", type, size, from + 1, objectPage.getTotalElements());
        return objectPage.map(objectMapper::toRestView);
    }

}