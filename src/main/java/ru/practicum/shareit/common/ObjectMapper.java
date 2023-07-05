package ru.practicum.shareit.common;

public interface ObjectMapper<E, T, C, V> {
    // E - тип данных объекта слоя репозитроиев (entity)
    // T - тип данных объекта сервисного слоя (domain type)
    // C - тип данных объекта, поступившего из внешней среды (command)
    // V - тип данных объекта, отправляемого во внешнюю среду (view)
    T fromRestCommand(C commandObject);

    V toRestView(T object);

    T fromDbEntity(E objectEntity);

    E toDbEntity(T object);

}