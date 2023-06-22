package ru.practicum.shareit.item.service;

import ru.practicum.shareit.common.crud.CrudService;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemService extends CrudService<Item> {

    List<Item> searchInNamesAndDescriptionsByText(long userId, String text);

}