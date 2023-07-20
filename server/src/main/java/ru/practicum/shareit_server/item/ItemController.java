package ru.practicum.shareit_server.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit_server.item.comment.Comment;
import ru.practicum.shareit_server.item.comment.dto.CommentRestCommand;
import ru.practicum.shareit_server.item.dto.ItemRestCommand;
import ru.practicum.shareit_server.item.dto.ItemRestView;

import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemRestView save(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
                             @RequestBody ItemRestCommand itemRestCommand) {
        return itemService.save(userId, itemRestCommand);
    }

    @GetMapping
    public List<ItemRestView> getAll(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemService.getAll(userId, from, size).toList();
    }

    @GetMapping("{item_id}")
    public ItemRestView getById(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @PathVariable(name = "item_id") long itemId) {
        return itemService.getById(userId, itemId);
    }

    @PatchMapping("{item_id}")
    public ItemRestView update(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @PathVariable(name = "item_id") long itemId,
            @RequestBody ItemRestCommand itemRestCommand) {
        return itemService.update(userId, itemId, itemRestCommand);
    }

    @DeleteMapping
    public void deleteAll(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId) {
        itemService.deleteAll(userId);
    }

    @DeleteMapping("{item_id}")
    public ItemRestView deleteById(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @PathVariable(name = "item_id") long itemId) {
        return itemService.deleteById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemRestView> searchInItemsNamesAndDescriptionByText(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @RequestParam(name = "text", defaultValue = "") String text,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemService.searchInNamesAndDescriptionsByText(userId, text, from, size).toList();
    }

    @PostMapping("{item_id}/comment")
    public Comment saveNewComment(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @PathVariable(name = "item_id") long itemId,
            @RequestBody CommentRestCommand commentRestCommand) {
        return itemService.addCommentToItem(userId, itemId, commentRestCommand);
    }

}