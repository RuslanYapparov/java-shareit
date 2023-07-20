package ru.practicum.shareit_server.item;

import org.springframework.data.domain.Page;

import ru.practicum.shareit_server.common.CrudService;
import ru.practicum.shareit_server.item.comment.Comment;
import ru.practicum.shareit_server.item.comment.dto.CommentRestCommand;
import ru.practicum.shareit_server.item.dto.ItemRestCommand;
import ru.practicum.shareit_server.item.dto.ItemRestView;

public interface ItemService extends CrudService<ItemRestCommand, ItemRestView> {

    Page<ItemRestView> searchInNamesAndDescriptionsByText(long userId, String text, int from, int size);

    Comment addCommentToItem(long authorId, long itemId, CommentRestCommand commentRestCommand);

}