package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;

import ru.practicum.shareit.common.CrudService;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentRestCommand;
import ru.practicum.shareit.item.dto.ItemRestCommand;
import ru.practicum.shareit.item.dto.ItemRestView;

public interface ItemService extends CrudService<ItemRestCommand, ItemRestView> {

    Page<ItemRestView> searchInNamesAndDescriptionsByText(long userId, String text, int from, int size);

    Comment addCommentToItem(long authorId, long itemId, CommentRestCommand commentRestCommand);

}