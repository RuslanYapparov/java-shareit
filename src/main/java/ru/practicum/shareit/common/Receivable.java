package ru.practicum.shareit.common;

public interface Receivable<C> {
    // C - тип данных объекта, поступающего для сохранения/обновления из внешней среды (command)
    C insertDefaultValuesInNullFields();

    void checkFieldsForDefaultAndIncorrectValues();

}