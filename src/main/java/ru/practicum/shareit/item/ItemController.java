package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import ru.practicum.shareit.common.RestViewListMapper;
import ru.practicum.shareit.item.dto.ItemRestCommand;
import ru.practicum.shareit.item.dto.ItemRestView;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final RestViewListMapper restViewListMapper;

    @PostMapping
    public ItemRestView save(@RequestHeader(value = "X-Sharer-User-Id") @Positive long userId,
                             @RequestBody @Valid ItemRestCommand itemRestCommand) {
        itemRestCommand = itemRestCommand.toBuilder()
                .ownerId(userId)
                .build();
        Item item = itemService.save(userId, itemMapper.fromRestCommand(itemRestCommand));
        return itemMapper.toRestView(item);
    }

    @GetMapping
    public List<ItemRestView> getAll(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId) {
        return restViewListMapper.mapListOfItems(itemService.getAll(userId));
    }

    @GetMapping("{item_id}")
    public ItemRestView getById(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @PathVariable(name = "item_id") @Positive long itemId) {
        return itemMapper.toRestView(itemService.getById(userId, itemId));
    }

    @PatchMapping("{item_id}")
    public ItemRestView update(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @Positive long userId,
            @PathVariable(name = "item_id") @Positive long itemId,
            @RequestBody ItemRestCommand itemRestCommand) {
        itemRestCommand = itemRestCommand.toBuilder()
                .ownerId(userId)
                .build();
        Item item = itemService.update(userId, itemId, itemMapper.fromRestCommand(itemRestCommand));
        return itemMapper.toRestView(item);
    }

    @DeleteMapping
    public void deleteAll(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @Positive long userId) {
        itemService.deleteAll(userId);
    }

    @DeleteMapping("{item_id}")
    public ItemRestView deleteById(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @PathVariable(name = "item_id") @Positive long itemId) {
        return itemMapper.toRestView(itemService.deleteById(userId, itemId));
    }

    @GetMapping("/search")
    public List<ItemRestView> searchInItemsNamesAndDescriptionByText(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @Positive long userId,
            @RequestParam(name = "text", defaultValue = "") String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemService.searchInNamesAndDescriptionsByText(userId, text);
        return restViewListMapper.mapListOfItems(items);
    }

}