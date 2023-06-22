package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;

import ru.practicum.shareit.common.ShareItConstants;
import ru.practicum.shareit.common.crud.default_implementations.InMemoryCrudDaoImpl;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemDaoImpl extends InMemoryCrudDaoImpl<Item> {

    public InMemoryItemDaoImpl() {
        super();
        type = "item";
    }

    @Override
    public Item update(long itemId, Item item) {
        long newItemOwnerId = item.getOwnerId();
        String itemName = item.getName();
        String itemDescription = item.getDescription();
        Boolean isNewItemAvailable = item.getIsAvailable();
        Item savedItem = getById(itemId);
        long savedItemOwnerId = savedItem.getOwnerId();
        if (savedItemOwnerId != newItemOwnerId) {
            throw new ObjectNotFoundException(String.format("Указанный в заголовке Http-запроса идентификатор " +
                            "пользователя-хозяина id'%d' для вещи '%s' не совпадает с сохраненным ранее id'%d'",
                    savedItemOwnerId, item.getName(), newItemOwnerId));
        }

        savedItem = savedItem.toBuilder()
                .name(ShareItConstants.NOT_ASSIGNED.equals(itemName) ? savedItem.getName() : itemName)
                .description(ShareItConstants.NOT_ASSIGNED.equals(itemDescription) ?
                        savedItem.getDescription() : itemDescription)
                .isAvailable(isNewItemAvailable == null ? savedItem.getIsAvailable() : isNewItemAvailable)
                .build();
        dataMap.put(itemId, savedItem);
        return getById(itemId);
    }

    @Override
    public List<Item> getAllEntitiesOfUserById(long userId) {
        return getAll().stream()
                .filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toList());
    }

}