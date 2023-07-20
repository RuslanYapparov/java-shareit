package ru.practicum.shareit_server.item;

import org.junit.jupiter.api.Test;

import ru.practicum.shareit_server.common.ShareItConstants;
import ru.practicum.shareit_server.item.comment.Comment;
import ru.practicum.shareit_server.item.dao.ItemEntity;
import ru.practicum.shareit_server.item.dto.ItemRestCommand;
import ru.practicum.shareit_server.item.dto.ItemRestView;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {
    private final ItemMapper itemMapper = new ItemMapperImpl();

    @Test
    public void fromRestCommand_whenGetRestCommand_thenReturnItemFromRestCommand() {
        ItemRestCommand itemRestCommand = initializeNewItemRestCommand();
        assertNotNull(itemMapper.fromRestCommand(itemRestCommand));
        assertNotEquals(Item.builder().build(), itemMapper.fromRestCommand(itemRestCommand));
        assertEquals(Item.builder().build(), itemMapper.fromRestCommand(ItemRestCommand.builder().build()));
        assertEquals(initializeNewItemFromRestCommand(), itemMapper.fromRestCommand(itemRestCommand));
    }

    @Test
    public void toRestView_whenGetItem_thenReturnItemRestView() {
        Item item = initializeNewItem();
        ItemRestView itemRestView = initializeNewItemRestView();
        assertNotNull(itemMapper.toRestView(item));
        assertNotEquals(itemRestView, itemMapper.toRestView(Item.builder().build()));
        assertEquals(ItemRestView.builder().build(), itemMapper.toRestView(Item.builder().build()));
        assertEquals(itemRestView, itemMapper.toRestView(item));
    }

    @Test
    public void fromDbEntity_whenGetItemEntity_thenReturnItem() {
        ItemEntity itemEntity = initializeNewItemEntity();
        Item item = initializeNewItem();
        item = item.toBuilder()
                .requestId(0)
                .comments(Collections.emptyList())
                .lastBooking(null)
                .build();
        assertNotNull(itemMapper.fromDbEntity(itemEntity));
        assertNotEquals(Item.builder().build(), itemMapper.fromDbEntity(itemEntity));
        assertEquals(item, itemMapper.fromDbEntity(itemEntity));
    }

    @Test
    public void toDbEntity_whenGetItem_thenReturnItemEntity() {
        ItemEntity itemEntity = initializeNewItemEntity();
        Item item = initializeNewItem();
        item = item.toBuilder()
                .requestId(0)
                .comments(Collections.emptyList())
                .lastBooking(null)
                .build();
        assertNotNull(itemMapper.toDbEntity(item));
        assertNotEquals(new ItemEntity(), itemMapper.toDbEntity(item));
        assertEquals(itemEntity, itemMapper.toDbEntity(item));
    }

    private ItemRestCommand initializeNewItemRestCommand() {
        return ItemRestCommand.builder()
                .requestId(1L)
                .name("item_name")
                .description("item_description")
                .available(true)
                .build();
    }

    private ItemRestView initializeNewItemRestView() {
        ItemBooking itemBooking = new ItemBooking(1L, 1L, "APPROVED",
                ShareItConstants.DEFAULT_LOCAL_DATE_TIME, ShareItConstants.DEFAULT_LOCAL_DATE_TIME);
        Comment comment = Comment.builder()
                .id(1L)
                .authorId(1L)
                .authorName("user_name")
                .text("comment_text")
                .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .build();
        return ItemRestView.builder()
                .id(1L)
                .requestId(1L)
                .ownerId(1L)
                .name("item_name")
                .description("item_description")
                .available(true)
                .lastBooking(itemBooking)
                .comments(List.of(comment))
                .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .itemPhotoUri(ShareItConstants.DEFAULT_URI)
                .rent(1.1F)
                .itemRating(1.1F)
                .build();
    }

    private Item initializeNewItem() {
        ItemBooking itemBooking = new ItemBooking(1L, 1L, "APPROVED",
                ShareItConstants.DEFAULT_LOCAL_DATE_TIME, ShareItConstants.DEFAULT_LOCAL_DATE_TIME);
        Comment comment = Comment.builder()
                .id(1L)
                .authorId(1L)
                .authorName("user_name")
                .text("comment_text")
                .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .build();
        return Item.builder()
                .id(1L)
                .requestId(1L)
                .ownerId(1L)
                .name("item_name")
                .description("item_description")
                .available(true)
                .lastBooking(itemBooking)
                .comments(List.of(comment))
                .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .itemPhotoUri(ShareItConstants.DEFAULT_URI)
                .rent(1.1F)
                .itemRating(1.1F)
                .build();
    }

    private Item initializeNewItemFromRestCommand() {
        return Item.builder()
                .requestId(1L)
                .name("item_name")
                .description("item_description")
                .available(true)
                .build();
    }

    private ItemEntity initializeNewItemEntity() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(1);
        itemEntity.setUserId(1);
        itemEntity.setName("item_name");
        itemEntity.setDescription("item_description");
        itemEntity.setAvailable(true);
        itemEntity.setCreated(ShareItConstants.DEFAULT_LOCAL_DATE_TIME);
        itemEntity.setLastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME);
        itemEntity.setItemPhotoUri(ShareItConstants.DEFAULT_URI.getPath());
        itemEntity.setRent(1.1F);
        itemEntity.setItemRating(1.1F);
        itemEntity.setComments(Collections.emptyList());
        return itemEntity;
    }

}