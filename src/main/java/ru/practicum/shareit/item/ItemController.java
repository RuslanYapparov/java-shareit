package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import ru.practicum.shareit.common.CrudApi;
import ru.practicum.shareit.common.RestViewListMapper;
import ru.practicum.shareit.item.dto.ItemRestCommand;
import ru.practicum.shareit.item.dto.ItemRestView;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController implements CrudApi<ItemRestCommand, ItemRestView> {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final RestViewListMapper restViewListMapper;

    @Override
    @PostMapping
    public ItemRestView save(@RequestHeader(value = "X-Sharer-User-Id") @Positive long userId,
                             @RequestBody @Valid ItemRestCommand itemRestCommand) {
        itemService.checkUserExisting(userId);
        itemRestCommand.setOwnerId(userId);
        Item item = itemService.save(itemMapper.fromRestCommand(itemRestCommand));
        return itemMapper.toRestView(item);
    }

    @Override
    @GetMapping
    public List<ItemRestView> getAll(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId) {
        List<Item> items;
        if (userId == 0) {
            items = itemService.getAll();
        } else {
            itemService.checkUserExisting(userId);
            items = itemService.getAllItemsOfUserById(userId);
        }
        return restViewListMapper.mapListOfItems(items);
    }

    @Override
    @GetMapping("{item_id}")
    public ItemRestView getById(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @PathVariable(name = "item_id") @Positive long itemId) {
        itemService.checkUserExisting(userId);
        return itemMapper.toRestView(itemService.getById(itemId));
    }

    @Override
    @PatchMapping("{item_id}")
    public ItemRestView update(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @Positive long userId,
            @PathVariable(name = "item_id") @Positive long itemId,
            @RequestBody ItemRestCommand itemRestCommand) {
        itemService.checkUserExisting(userId);
        itemRestCommand.setOwnerId(userId);
        Item item = itemService.update(itemId, itemMapper.fromRestCommand(itemRestCommand));
        return itemMapper.toRestView(item);
    }

    @Override
    @DeleteMapping
    public void deleteAll(@RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @Positive long userId) {
        if (userId == 0) {
            itemService.deleteAll();
        } else {
            itemService.checkUserExisting(userId);
            itemService.deleteAllItemsOfUserById(userId);
        }
    }

    @Override
    @DeleteMapping("{item_id}")
    public ItemRestView deleteById(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @PathVariable(name = "item_id") @Positive long itemId) {
        itemService.checkUserExisting(userId);
        return itemMapper.toRestView(itemService.deleteById(itemId));
    }

    @GetMapping("/search")
    public List<ItemRestView> searchInItemsNamesAndDescriptionByText(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @Positive long userId,
            @RequestParam(name = "text", defaultValue = "") String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        if (userId != 0) {
            itemService.checkUserExisting(userId);
        }
        List<Item> items = itemService.searchInNamesAndDescriptionsByText(text);
        return restViewListMapper.mapListOfItems(items);
    }

}