package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dao.ItemEntity;
import ru.practicum.shareit.request.dao.ItemRequestEntity;
import ru.practicum.shareit.request.dto.ItemRequestRestCommand;
import ru.practicum.shareit.request.dto.ItemRequestRestView;
import ru.practicum.shareit.request.dto.RequestedItem;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest  {
    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();
    private ItemRequest itemRequest = ItemRequest.builder()
            .id(1)
            .requesterId(1)
            .description("description")
            .created(LocalDateTime.of(2023, 7, 7, 7, 7, 7))
            .lastModified(LocalDateTime.of(2023, 8, 8, 8, 8, 8))
            .items(List.of(RequestedItem.builder().requestId(1).build()))
            .build();
    private final ItemRequest itemRequestFromRestCommand = ItemRequest.builder()
            .description("description")
            .build();
    private final ItemRequestRestCommand itemRequestRestCommand = initializeNewItemRequestRestCommand();
    private final ItemRequestRestView itemRequestRestView = ItemRequestRestView.builder()
            .id(1)
            .requesterId(1)
            .description("description")
            .created(LocalDateTime.of(2023, 7, 7, 7, 7, 7))
            .lastModified(LocalDateTime.of(2023, 8, 8, 8, 8, 8))
            .items(List.of(RequestedItem.builder().requestId(1).build()))
            .build();
    private ItemRequestEntity itemRequestEntity = initializeNewItemRequestEntity();

    @Test
    public void fromRestCommand_whenGetRestCommand_thenReturnItemRequestFromRestCommand() {
        assertNotNull(itemRequestMapper.fromRestCommand(itemRequestRestCommand));
        assertNotEquals(ItemRequest.builder().build(), itemRequestMapper.fromRestCommand(itemRequestRestCommand));
        assertEquals(ItemRequest.builder().build(), itemRequestMapper.fromRestCommand(new ItemRequestRestCommand()));
        assertEquals(itemRequestFromRestCommand, itemRequestMapper.fromRestCommand(itemRequestRestCommand));
    }

    @Test
    public void toRestView_whenGetItemRequest_thenReturnItemRequestRestView() {
        assertNotNull(itemRequestMapper.toRestView(itemRequest));
        assertNotEquals(itemRequestRestView, itemRequestMapper.toRestView(ItemRequest.builder().build()));
        assertEquals(ItemRequestRestView.builder().build(), itemRequestMapper.toRestView(ItemRequest.builder().build()));
        assertEquals(itemRequestRestView, itemRequestMapper.toRestView(itemRequest));
    }

    @Test
    public void fromDbEntity_whenGetItemRequestEntity_thenReturnItemRequest() {
        assertNotNull(itemRequestMapper.fromDbEntity(itemRequestEntity));
        assertNotEquals(ItemRequest.builder().build(), itemRequestMapper.fromDbEntity(itemRequestEntity));
        assertEquals(ItemRequest.builder().build(), itemRequestMapper.fromDbEntity(new ItemRequestEntity()));
        assertEquals(itemRequest, itemRequestMapper.fromDbEntity(itemRequestEntity));
        assertEquals(1, itemRequestMapper.fromDbEntity(itemRequestEntity).getItems().size());
        assertEquals(RequestedItem.builder().requestId(1).build(),
                itemRequestMapper.fromDbEntity(itemRequestEntity).getItems().get(0));
    }

    @Test
    public void toDbEntity_whenGetItemRequest_thenReturnItemRequestEntity() {
        assertNotNull(itemRequestMapper.toDbEntity(itemRequest));
        assertNotEquals(new ItemRequestEntity(), itemRequestMapper.toDbEntity(itemRequest));
        assertEquals(new ItemRequestEntity(), itemRequestMapper.toDbEntity(ItemRequest.builder().build()));
        itemRequestEntity.setItems(Collections.emptyList());
        /* Метод hashCode() вызывает StackOverflowError при вызове у зависящих друг от руга объектов, поэтому установим
        * пустой лист как заглушку для прохождения теста, сделав соответствующие изменения в сравниваемом объекте */
        itemRequest = itemRequest.toBuilder().items(Collections.emptyList()).build();
        assertEquals(itemRequestEntity, itemRequestMapper.toDbEntity(itemRequest));
        itemRequest = itemRequest.toBuilder().items(List.of(RequestedItem.builder().id(1).build())).build();
        itemRequestEntity = initializeNewItemRequestEntity();
    }

    private ItemRequestEntity initializeNewItemRequestEntity() {
        ItemRequestEntity itemRequestEntity = new ItemRequestEntity();
        itemRequestEntity.setId(1);
        itemRequestEntity.setUserId(1);
        itemRequestEntity.setDescription("description");
        itemRequestEntity.setCreated(LocalDateTime.of(2023, 7, 7, 7, 7, 7));
        itemRequestEntity.setLastModified(LocalDateTime.of(2023, 8, 8, 8, 8, 8));
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setRequest(itemRequestEntity);
        itemRequestEntity.setItems(List.of(itemEntity));
        return itemRequestEntity;
    }

    private ItemRequestRestCommand initializeNewItemRequestRestCommand() {
        ItemRequestRestCommand itemRequestRestCommand = new ItemRequestRestCommand();
        itemRequestRestCommand.setDescription("description");
        return itemRequestRestCommand;
    }

}