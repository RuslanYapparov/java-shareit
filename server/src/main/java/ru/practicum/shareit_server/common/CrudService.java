package ru.practicum.shareit_server.common;

import org.springframework.data.domain.Page;

public interface CrudService<C, V> {
    // C - тип данных объекта, поступившего из внешней среды (command)
    // V - тип данных объекта, отправляемого во внешнюю среду (view)
    V save(long userId, C commandObject);

    Page<V> getAll(long userId, int from, int size);

    V getById(long userId, long id);

    V update(long userId, long id, C commandObject);

    void deleteAll(long userId);

    V deleteById(long userId, long id);

}