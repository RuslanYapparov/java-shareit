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
import ru.practicum.shareit_gateway.util.EndpointObjectsValidator;

@Validated
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> save(
            @PositiveOrZero @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @Valid @RequestBody ItemRestCommand itemRestCommand) {
        log.debug("Gateway received ru.practicum.shareit.request to create an ru.practicum.shareit.item, userId={}, ru.practicum.shareit.item={}", userId, itemRestCommand);
        EndpointObjectsValidator.checkUserIdForNullValue(userId, "сохранение");
        return itemClient.save(userId, itemRestCommand);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(
            @PositiveOrZero @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        log.debug("Gateway received ru.practicum.shareit.request to get all items of ru.practicum.shareit.user, userId={}, from={}, size={}", userId, from, size);
        EndpointObjectsValidator.checkUserIdForNullValue(userId, "получение всех объектов, сохраненных пользователем");
        EndpointObjectsValidator.checkPaginationParameters(from, size);
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("{item_id}")
    public ResponseEntity<Object> getById(
            @PositiveOrZero @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @Positive @PathVariable(name = "item_id") long itemId) {
        log.debug("Gateway received ru.practicum.shareit.request to get ru.practicum.shareit.item by id{}, userId={}", itemId, userId);
        EndpointObjectsValidator.checkUserIdForNullValue(userId, "получение по идентификатору");
        return itemClient.getById(userId, itemId);
    }

    @PatchMapping("{item_id}")
    public ResponseEntity<Object> update(
            @PositiveOrZero @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @Positive @PathVariable(name = "item_id") long itemId,
            @RequestBody ItemRestCommand itemRestCommand) {
        log.debug("Gateway received ru.practicum.shareit.request to update ru.practicum.shareit.item, userId={}, itemId={}", userId, itemId);
        EndpointObjectsValidator.checkUserIdForNullValue(userId, "обновление");
        return itemClient.update(userId, itemId, itemRestCommand);
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteAll(
            @PositiveOrZero @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId) {
        log.debug("Gateway received ru.practicum.shareit.request to delete all items of ru.practicum.shareit.user, userId={}", userId);
        EndpointObjectsValidator.checkUserIdForNullValue(userId, "удаление всех объектов, сохраненных пользователем");
        return itemClient.deleteAll(userId);
    }

    @DeleteMapping("{item_id}")
    public ResponseEntity<Object> deleteById(
            @PositiveOrZero @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @Positive @PathVariable(name = "item_id") long itemId) {
        log.debug("Gateway received ru.practicum.shareit.request to delete ru.practicum.shareit.item of ru.practicum.shareit.user by id, userId={}, itemId={}", userId, itemId);
        EndpointObjectsValidator.checkUserIdForNullValue(userId, "удаление объекта по идентификатору");
        return itemClient.deleteById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchInItemsNamesAndDescriptionByText(
            @PositiveOrZero @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @RequestParam(name = "text", defaultValue = "") String text,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        log.debug("Gateway received ru.practicum.shareit.request to search in all items names and descriptions by text, userId={}, " +
                "text={}, from={}, size={}", userId, text, from, size);
        EndpointObjectsValidator.checkUserIdForNullValue(userId, "поиск по имени и описанию");
        EndpointObjectsValidator.checkPaginationParameters(from, size);
        return itemClient.searchInNamesAndDescriptionsByText(userId, text, from, size);
    }

    @PostMapping("{item_id}/comment")
    public ResponseEntity<Object> saveNewComment(
            @PositiveOrZero @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long authorId,
            @Positive @PathVariable(name = "item_id") long itemId,
            @Valid @RequestBody CommentRestCommand commentRestCommand) {
        log.debug("Gateway received ru.practicum.shareit.request to create comment for ru.practicum.shareit.item, authorId={}, itemId={}, comment={}",
                authorId, itemId, commentRestCommand);
        EndpointObjectsValidator.checkUserIdForNullValue(authorId, "сохранение нового комментария");
        return itemClient.addCommentToItem(authorId, itemId, commentRestCommand);
    }

}