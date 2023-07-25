package ru.practicum.shareit_server.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit_server.request.dto.RequestedItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {
    private final ItemRequest itemRequestWithNullFields = ItemRequest.builder().build();
    private final ItemRequest itemRequestWithAllFields = ItemRequest.builder()
            .id(1)
            .requesterId(1)
            .description("description")
            .created(LocalDateTime.of(2023, 7, 1, 12, 1, 1))
            .lastModified(LocalDateTime.of(2023, 7, 1, 12, 1, 2))
            .items(new ArrayList<>())
            .build();
    private final ItemRequest itemRequestWithNullId = itemRequestWithAllFields.toBuilder()
            .id(0)
            .build();
    private final ItemRequest itemRequestWithNullRequesterId = itemRequestWithAllFields.toBuilder()
            .requesterId(0)
            .build();
    private final ItemRequest itemRequestWithNullDescription = itemRequestWithAllFields.toBuilder()
            .description(null)
            .build();
    private final ItemRequest itemRequestWithAnotherDescription = itemRequestWithAllFields.toBuilder()
            .description("anotherDescription")
            .build();
    private final ItemRequest itemRequestWithNullCreated = itemRequestWithAllFields.toBuilder()
            .created(null)
            .build();
    private final ItemRequest itemRequestWithNullLastModified = itemRequestWithAllFields.toBuilder()
            .lastModified(null)
            .build();
    private final ItemRequest itemRequestWithNullItems = itemRequestWithAllFields.toBuilder()
            .items(null)
            .build();
    private final ItemRequest itemRequestWithOneItemEntityInItems = itemRequestWithAllFields.toBuilder()
            .items(new ArrayList<>(List.of(RequestedItem.builder().build())))
            .build();

    @Test
    void testEquals() {
        assertEquals(itemRequestWithAllFields, itemRequestWithAllFields.toBuilder().build());
        assertNotEquals(itemRequestWithAllFields, itemRequestWithNullFields);
        assertNotEquals(itemRequestWithAllFields, itemRequestWithNullId);
        assertNotEquals(itemRequestWithAllFields, itemRequestWithNullRequesterId);
        assertNotEquals(itemRequestWithAllFields, itemRequestWithNullDescription);
        assertNotEquals(itemRequestWithAllFields, itemRequestWithAnotherDescription);
        assertNotEquals(itemRequestWithAllFields, itemRequestWithNullCreated);
        assertNotEquals(itemRequestWithAllFields, itemRequestWithNullLastModified);
        assertNotEquals(itemRequestWithAllFields, itemRequestWithNullItems);
        assertNotEquals(itemRequestWithAllFields, itemRequestWithOneItemEntityInItems);
    }

    @Test
    void testHashCode() {
        assertEquals(itemRequestWithAllFields.hashCode(), itemRequestWithAllFields.toBuilder().build().hashCode());
        assertNotEquals(itemRequestWithAllFields.hashCode(), itemRequestWithNullFields.hashCode());
        assertNotEquals(itemRequestWithAllFields.hashCode(), itemRequestWithNullId.hashCode());
        assertNotEquals(itemRequestWithAllFields.hashCode(), itemRequestWithNullRequesterId.hashCode());
        assertNotEquals(itemRequestWithAllFields.hashCode(), itemRequestWithNullDescription.hashCode());
        assertNotEquals(itemRequestWithAllFields.hashCode(), itemRequestWithAnotherDescription.hashCode());
        assertNotEquals(itemRequestWithAllFields.hashCode(), itemRequestWithNullCreated.hashCode());
        assertNotEquals(itemRequestWithAllFields.hashCode(), itemRequestWithNullLastModified.hashCode());
        assertNotEquals(itemRequestWithAllFields.hashCode(), itemRequestWithNullItems.hashCode());
        assertNotEquals(itemRequestWithAllFields.hashCode(), itemRequestWithOneItemEntityInItems.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("ItemRequest(id=1, requesterId=1, description=description, created=2023-07-01T12:01:01, " +
                "lastModified=2023-07-01T12:01:02, items=[])", itemRequestWithAllFields.toString());
    }

}