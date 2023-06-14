package ru.practicum.shareit.item.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.common.default_implementations.CrudServiceImpl;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl extends CrudServiceImpl<Item> implements ItemService {
    private final ItemDao itemDao;

    public ItemServiceImpl(ItemDao itemDao, UserDao userDao) {
        this.objectDao = itemDao;
        this.itemDao = itemDao;
        this.userDao = userDao;
        this.type = "item";
    }

    @Override
    public List<Item> getAllItemsOfUserById(long userId) {
        List<Item> items = itemDao.getAllItemsOfUserById(userId);
        log.info(String.format("Запрошен список всех вещей, сохраненных у пользователя с id'%d'", userId));
        return items;
    }

    @Override
    public void deleteAllItemsOfUserById(long userId) {
        itemDao.deleteAllItemsOfUserById(userId);
        log.info(String.format("Удалены данные всех вещей, сохраненные у пользователя с id'%d'", userId));
    }

    @Override
    public List<Item> searchInNamesAndDescriptionsByText(String text) {
        List<Item> items = itemDao.getAll().stream()
                .filter(Item::getIsAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
        log.info(String.format("Произведен поиск всех вещей у всех пользователей, в названии и/или описании которых " +
                "присутствует текст '%s'. Найдено всего %d таких вещей", text, items.size()));
        return items;
    }

}