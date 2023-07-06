package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import ru.practicum.shareit.exception.BadRequestHeaderException;
import ru.practicum.shareit.exception.BadRequestParameterException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentRestCommand;
import ru.practicum.shareit.item.dto.ItemRestCommand;
import ru.practicum.shareit.item.dto.ItemRestView;

import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemRestView save(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
                             @RequestBody @Valid ItemRestCommand itemRestCommand) {
        checkUserIdForNullValue(userId, "сохранение");
        return itemService.save(userId, itemRestCommand);
    }

    @GetMapping
    public List<ItemRestView> getAll(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero int size) {
        checkUserIdForNullValue(userId, "получение всех объектов, сохраненных пользователем");
        checkPaginationParameters(from, size);
        return itemService.getAll(userId, from, size).toList();
    }

    @GetMapping("{item_id}")
    public ItemRestView getById(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @PathVariable(name = "item_id") @Positive long itemId) {
        checkUserIdForNullValue(userId, "получение");
        return itemService.getById(userId, itemId);
    }

    @PatchMapping("{item_id}")
    public ItemRestView update(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @PathVariable(name = "item_id") @Positive long itemId,
            @RequestBody ItemRestCommand itemRestCommand) {
        checkUserIdForNullValue(userId, "обновление");
        return itemService.update(userId, itemId, itemRestCommand);
    }

    @DeleteMapping
    public void deleteAll(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId) {
        checkUserIdForNullValue(userId, "удаление всех объектов, сохраненных пользователем");
        itemService.deleteAll(userId);
    }

    @DeleteMapping("{item_id}")
    public ItemRestView deleteById(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @PathVariable(name = "item_id") @Positive long itemId) {
        checkUserIdForNullValue(userId, "удаление");
        return itemService.deleteById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemRestView> searchInItemsNamesAndDescriptionByText(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @RequestParam(name = "text", defaultValue = "") String text,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero int size) {
        checkUserIdForNullValue(userId, "поиск по имени и описанию");
        checkPaginationParameters(from, size);
        if (text.isBlank()) {
            return List.of();
        }
        return itemService.searchInNamesAndDescriptionsByText(userId, text, from, size).toList();
    }

    @PostMapping("{item_id}/comment")
    public Comment saveNewComment(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @PathVariable(name = "item_id") @Positive long itemId,
            @RequestBody @Valid CommentRestCommand commentRestCommand) {
        checkUserIdForNullValue(userId, "сохранение нового комментария");
        return itemService.addCommentToItem(userId, itemId, commentRestCommand);
    }

    private void checkUserIdForNullValue(long userId, String operation) {
        if (userId == 0L) {
            throw new BadRequestHeaderException(String.format("В заголовке запроса на проведение операции '%s' " +
                    "с данными объекта 'вещь' не передан идентификатор пользователя, либо указан 0", operation));
        }
    }

    private void checkPaginationParameters(int from, int size) {
        if (from < 0) {
            throw new BadRequestParameterException("В параметре запроса указано неверное значение порядкового номера " +
            "первого отображаемого элемента '" + from + "'. Значение данного параметра не должно быть меньше 0");
        }
        if (size <= 0) {
            throw new BadRequestParameterException("В параметре запроса указано неверное значение количества " +
            "отображаемых элементов, равное '" + size + "'. Значение данного параметра не должно быть меньше, чем 1");
        }
    }

}