package ru.practicum.shareit.item;

import ru.practicum.shareit.common.CrudService;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentRestCommand;
import ru.practicum.shareit.item.dto.ItemRestCommand;
import ru.practicum.shareit.item.dto.ItemRestView;

import java.util.List;

public interface ItemService extends CrudService<ItemRestCommand, ItemRestView> {

    List<ItemRestView> searchInNamesAndDescriptionsByText(long userId, String text);

    Comment addCommentToItem(long authorId, long itemId, CommentRestCommand commentRestCommand);

}