package ru.practicum.shareit_server.common;

import org.springframework.data.domain.Page;

public interface CrudService<C, V> {
    // C - тип данных объекта, поступившего из внешней среды (command)
    // V - тип данных объекта, отправляемого во внешнюю среду (view)
    V save(long userId, C commandObject);                // Переопределяется в сервисе любой сущности, связанной с User

    Page<V> getAll(long userId, int from, int size);     // Переопределяется в сервисе любой сущности, связанной с User

    V getById(long userId, long id);

    V update(long userId, long id, C commandObject);     // Переопределяется в сервисе любой сущности, связанной с User

    void deleteAll(long userId);                         // Переопределяется в сервисе любой сущности, связанной с User

    V deleteById(long userId, long id);

}