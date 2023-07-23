package ru.practicum.shareit_gateway.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import ru.practicum.shareit_gateway.item.dto.CommentRestCommand;
import ru.practicum.shareit_gateway.item.dto.ItemRestCommand;

@Validated
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> save(
            @Positive @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @Valid @RequestBody ItemRestCommand itemRestCommand) {
        log.debug("Gateway received request to create an item, userId={}, item={}", userId, itemRestCommand);
        return itemClient.save(userId, itemRestCommand);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(
            @Positive @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.debug("Gateway received request to get all items of user, userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("{item_id}")
    public ResponseEntity<Object> getById(
            @Positive @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @Positive @PathVariable(name = "item_id") long itemId) {
        log.debug("Gateway received request to get item by id{}, userId={}", itemId, userId);
        return itemClient.getById(userId, itemId);
    }

    @PatchMapping("{item_id}")
    public ResponseEntity<Object> update(
            @Positive @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @Positive @PathVariable(name = "item_id") long itemId,
            @RequestBody ItemRestCommand itemRestCommand) {
        log.debug("Gateway received request to update item, userId={}, itemId={}", userId, itemId);
        return itemClient.update(userId, itemId, itemRestCommand);
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteAll(
            @Positive @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId) {
        log.debug("Gateway received request to delete all items of user, userId={}", userId);
        return itemClient.deleteAll(userId);
    }

    @DeleteMapping("{item_id}")
    public ResponseEntity<Object> deleteById(
            @Positive @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @Positive @PathVariable(name = "item_id") long itemId) {
        log.debug("Gateway received request to delete item of user by id, userId={}, itemId={}", userId, itemId);
        return itemClient.deleteById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchInItemsNamesAndDescriptionByText(
            @Positive @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @RequestParam(name = "text", defaultValue = "") String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.debug("Gateway received request to search in all items names and descriptions by text, userId={}, " +
                "text={}, from={}, size={}", userId, text, from, size);
        return itemClient.searchInNamesAndDescriptionsByText(userId, text, from, size);
    }

    @PostMapping("{item_id}/comment")
    public ResponseEntity<Object> saveNewComment(
            @Positive @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long authorId,
            @Positive @PathVariable(name = "item_id") long itemId,
            @Valid @RequestBody CommentRestCommand commentRestCommand) {
        log.debug("Gateway received request to create comment for item, authorId={}, itemId={}, comment={}",
                authorId, itemId, commentRestCommand);
        return itemClient.addCommentToItem(authorId, itemId, commentRestCommand);
    }

}