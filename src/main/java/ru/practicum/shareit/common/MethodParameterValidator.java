package ru.practicum.shareit.common;

import ru.practicum.shareit.exception.BadRequestHeaderException;
import ru.practicum.shareit.exception.BadRequestParameterException;

public class MethodParameterValidator {

    public static void checkUserIdForNullValue(long userId, String operation) {
        if (userId <= 0L) {
            throw new BadRequestHeaderException(String.format("В заголовке запроса на проведение операции '%s' " +
                    "с данными объекта не передан идентификатор пользователя, либо указан 0", operation));
        }
    }

    public static void checkPaginationParameters(int from, int size) {
        if (from < 0) {
            throw new BadRequestParameterException("В параметре запроса указано неверное значение порядкового номера " +
                    "первого отображаемого элемента '" + from + "'. Значение данного параметра не должно быть меньше 0");
        }
        if (size <= 0) {
            throw new BadRequestParameterException("В параметре запроса указано неверное значение количества " +
                    "отображаемых элементов, равное '" + size + "'. Значение данного параметра не должно быть меньше, чем 1");
        }
    }

}