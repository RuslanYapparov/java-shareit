package ru.practicum.shareit.common;

public interface ObjectMapper<T, C, V> {
    // T - тип данных объекта доменного слоя (type)
    // C - тип данных объекта, поступающего для сохранения/обновления из внешней среды или сервиса (command)
    // V - класс объекта, отправляемого во внешнюю среду или сервис (view)
    T fromRestCommand(C objectRestCommand);

    V toRestView(T object);

}
