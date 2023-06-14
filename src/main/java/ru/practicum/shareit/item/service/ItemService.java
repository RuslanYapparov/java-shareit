package ru.practicum.shareit.item.service;

import ru.practicum.shareit.common.CrudService;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemService extends CrudService<Item> {

    List<Item> getAllItemsOfUserById(long userId);

    void deleteAllItemsOfUserById(long userId);

    List<Item> searchInNamesAndDescriptionsByText(String text);

}