package ru.practicum.shareit_server.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ru.practicum.shareit_server.common.CrudServiceImpl;
import ru.practicum.shareit_server.request.dao.ItemRequestEntity;
import ru.practicum.shareit_server.request.dao.ItemRequestRepository;
import ru.practicum.shareit_server.request.dto.ItemRequestRestCommand;
import ru.practicum.shareit_server.request.dto.ItemRequestRestView;
import ru.practicum.shareit_server.user.dao.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemRequestServiceImpl
        extends CrudServiceImpl<ItemRequestEntity, ItemRequest, ItemRequestRestCommand, ItemRequestRestView>
        implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  UserRepository userRepository,
                                  ItemRequestMapper itemRequestMapper
                                  ) {
        this.entityRepository = itemRequestRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.objectMapper = itemRequestMapper;
        this.type = "request";
    }

    @Override
    public ItemRequestRestView save(long userId, ItemRequestRestCommand itemRequestRestCommand) {
        checkExistingAndReturnUserShort(userId);
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
        checkExistingAndReturnUserShort(requesterId);
        List<ItemRequestEntity> itemRequestEntities =
                itemRequestRepository.findAllByUserIdOrderByCreatedDesc(requesterId);
        return itemRequestEntities.stream()
                .map(objectMapper::fromDbEntity)
                .map(objectMapper::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ItemRequestRestView> getAll(long userId, int from, int size) {
        Sort sortByCreatedDate = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(from / size, size, sortByCreatedDate);
        Page<ItemRequestEntity> itemRequestEntityPage = itemRequestRepository.findAllWithoutUsersRequests(userId, page);
        Page<ItemRequest> objectPage = itemRequestEntityPage.map(objectMapper::fromDbEntity);
        log.info("Запрошен постраничный список всех сохраненных объектов '{}'. Количество объектов для отображения " +
                "на странице - {}, объекты отображаются " +
                "по порядку с {} по {}", type, size, from + 1, objectPage.getTotalElements());
        return objectPage.map(objectMapper::toRestView);
    }

}