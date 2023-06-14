package ru.practicum.shareit.common;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import ru.practicum.shareit.exception.BadRequestHeaderException;

import java.util.List;

public interface CrudApi<C, V> {
    // C - тип данных объекта, поступающего для сохранения/обновления из внешней среды или сервиса (command)
    // V - класс объекта, отправляемого во внешнюю среду или сервис (view)
    V save(@Positive long userId, @Valid C commandObject) throws BadRequestHeaderException;

    List<V> getAll(@PositiveOrZero long userId) throws BadRequestHeaderException;

    V getById(@PositiveOrZero long userId, @Positive long objectId) throws BadRequestHeaderException;

    V update(@Positive long userId, @Positive long id, C commandObject) throws BadRequestHeaderException;

    void deleteAll(@PositiveOrZero long userId) throws BadRequestHeaderException;

    V deleteById(@PositiveOrZero long userId, @Positive long objectId) throws BadRequestHeaderException;

}