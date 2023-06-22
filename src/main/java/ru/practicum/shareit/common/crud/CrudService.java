package ru.practicum.shareit.common.crud;

import java.util.List;

public interface CrudService<T> {
    // T - тип данных объекта слоя бизнес-логики
    T save(long userId, T object);

    List<T> getAll(long userId);

    T getById(long userId, long id);

    int getQuantity();

    T update(long userId, long id, T object);

    void deleteAll(long userId);

    T deleteById(long userId, long id);

}