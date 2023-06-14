package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.common.CrudDao;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemDao extends CrudDao<Item> {

    List<Item> getAllItemsOfUserById(long userId);

    void deleteAllItemsOfUserById(long userId);

}
