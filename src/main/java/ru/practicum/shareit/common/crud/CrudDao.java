package ru.practicum.shareit.common.crud;

import java.util.List;

public interface CrudDao<T> {
    // T - тип данных объекта слоя бизнес-логики
    T save(T object);

    List<T> getAll();

    T getById(long id);

    int getQuantity();

    T update(long id, T object);

    void deleteAll();

    T deleteById(long id);

    List<T> getAllEntitiesOfUserById(long userId);

    List<T> deleteAllEntitiesOfUserById(long userId);

}