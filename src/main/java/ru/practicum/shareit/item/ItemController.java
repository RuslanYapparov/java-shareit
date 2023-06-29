package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentRestCommand;
import ru.practicum.shareit.item.dto.ItemRestCommand;
import ru.practicum.shareit.item.dto.ItemRestView;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemRestView save(@RequestHeader(value = "X-Sharer-User-Id") @Positive long userId,
                             @RequestBody @Valid ItemRestCommand itemRestCommand) {
        return itemService.save(userId, itemRestCommand);
    }

    @GetMapping
    public List<ItemRestView> getAll(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("{item_id}")
    public ItemRestView getById(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @PathVariable(name = "item_id") @Positive long itemId) {
        return itemService.getById(userId, itemId);
    }

    @PatchMapping("{item_id}")
    public ItemRestView update(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @Positive long userId,
            @PathVariable(name = "item_id") @Positive long itemId,
            @RequestBody ItemRestCommand itemRestCommand) {
        return itemService.update(userId, itemId, itemRestCommand);
    }

    @DeleteMapping
    public void deleteAll(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @Positive long userId) {
        itemService.deleteAll(userId);
    }

    @DeleteMapping("{item_id}")
    public ItemRestView deleteById(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @PathVariable(name = "item_id") @Positive long itemId) {
        return itemService.deleteById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemRestView> searchInItemsNamesAndDescriptionByText(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @Positive long userId,
            @RequestParam(name = "text", defaultValue = "") String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.searchInNamesAndDescriptionsByText(userId, text);
    }

    @PostMapping("{item_id}/comment")
    public Comment saveNewComment(@RequestHeader(value = "X-Sharer-User-Id") @Positive long userId,
                                  @PathVariable(name = "item_id") @Positive long itemId,
                                  @RequestBody @Valid CommentRestCommand commentRestCommand) {
        return itemService.addCommentToItem(userId, itemId, commentRestCommand);
    }

}