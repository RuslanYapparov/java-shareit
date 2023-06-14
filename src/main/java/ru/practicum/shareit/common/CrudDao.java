package ru.practicum.shareit.common;

import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.exception.ObjectNotFoundException;

import java.util.List;

public interface CrudDao<T> {
    // T - тип данных объекта слоя бизнес-логики
    T save(T object) throws ObjectAlreadyExistsException;

    List<T> getAll();

    T getById(long id) throws ObjectNotFoundException;

    int getQuantity();

    T update(long id, T object) throws ObjectNotFoundException;

    void deleteAll();

    T deleteById(long id) throws ObjectNotFoundException;

}