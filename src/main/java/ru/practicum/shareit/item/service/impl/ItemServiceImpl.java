package ru.practicum.shareit.item.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.common.crud.CrudDao;
import ru.practicum.shareit.common.crud.default_implementations.CrudServiceImpl;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl extends CrudServiceImpl<Item> implements ItemService {

    public ItemServiceImpl(CrudDao<Item> itemDao, UserDao userDao) {
        this.objectDao = itemDao;
        this.userDao = userDao;
        this.type = "item";
    }

    @Override
    public List<Item> searchInNamesAndDescriptionsByText(long userId, String text) {
        checkUserExisting(userId);
        List<Item> items = objectDao.getAll().stream()
                .filter(Item::getIsAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
        log.info("Произведен поиск всех вещей у всех пользователей, в названии и/или описании которых " +
                "присутствует текст '{}'. Найдено всего {} таких вещей", text, items.size());
        return items;
    }

}