package ru.practicum.shareit.common;

import ru.practicum.shareit.exception.BadRequestHeaderException;
import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.exception.ObjectNotFoundException;

import java.util.List;

public interface CrudService<T extends IdentificableObject> {
    // T - тип данных объекта слоя бизнес-логики
    T save(T object) throws ObjectAlreadyExistsException, BadRequestHeaderException;

    List<T> getAll() throws BadRequestHeaderException;

    T getById(long id) throws ObjectNotFoundException, BadRequestHeaderException;

    int getQuantity();

    T update(long id, T object) throws ObjectNotFoundException, BadRequestHeaderException;

    void deleteAll() throws BadRequestHeaderException;

    T deleteById(long id) throws ObjectNotFoundException, BadRequestHeaderException;

    void checkUserExisting(long userId) throws ObjectNotFoundException, BadRequestHeaderException;

}