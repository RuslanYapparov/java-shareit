package ru.practicum.shareit.item.dao.impl;

import org.springframework.stereotype.Repository;

import ru.practicum.shareit.common.ShareItConstants;
import ru.practicum.shareit.common.default_implementations.InMemoryCrudDaoImpl;
import ru.practicum.shareit.exception.BadRequestHeaderException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemDao;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemDaoImpl extends InMemoryCrudDaoImpl<Item> implements ItemDao {

    public InMemoryItemDaoImpl() {
        super();
        this.type = "item";
    }

    @Override
    public Item update(long itemId, Item item) throws ObjectNotFoundException, BadRequestHeaderException {
        long newItemOwnerId = item.getOwnerId();
        String itemName = item.getName();
        String itemDescription = item.getDescription();
        Boolean itemIsAvailable;
        try {
            itemIsAvailable = item.getIsAvailable();
        } catch (NullPointerException exception) {
            itemIsAvailable = null;
        }
        Item savedItem = this.getById(itemId);
        long savedItemOwnerId = savedItem.getOwnerId();
        if (savedItemOwnerId != newItemOwnerId) {
            throw new ObjectNotFoundException(String.format("Указанный в заголовке Http-запроса идентификатор " +
                            "пользователя-хозяина id'%d' для вещи '%s' не совпадает с сохраненным ранее id'%d'",
                    savedItemOwnerId, item.getName(), newItemOwnerId));
        }

        savedItem = savedItem.toBuilder()
                .name(itemName.equals(ShareItConstants.NOT_ASSIGNED) ? savedItem.getName() : itemName)
                .description(itemDescription.equals(ShareItConstants.NOT_ASSIGNED) ?
                        savedItem.getDescription() : itemDescription)
                .isAvailable(itemIsAvailable == null ? savedItem.getIsAvailable() : itemIsAvailable)
                .build();
        savedItem.setId(itemId);
        dataMap.put(itemId, savedItem);
        return this.getById(itemId);
    }

    @Override
    public List<Item> getAllItemsOfUserById(long userId) {
        return this.getAll().stream()
                .filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllItemsOfUserById(long userId) {
        this.getAllItemsOfUserById(userId).forEach(item -> dataMap.remove(item.getId()));
    }

}