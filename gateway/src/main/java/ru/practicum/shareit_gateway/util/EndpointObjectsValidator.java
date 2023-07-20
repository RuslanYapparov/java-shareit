package ru.practicum.shareit_gateway.util;

import lombok.experimental.UtilityClass;

import ru.practicum.shareit_gateway.exception.BadRequestBodyException;
import ru.practicum.shareit_gateway.exception.BadRequestHeaderException;
import ru.practicum.shareit_gateway.exception.BadRequestParameterException;

@UtilityClass
public class EndpointObjectsValidator {

    public void checkUserIdForNullValue(long userId, String operation) {
        if (userId <= 0L) {
            throw new BadRequestHeaderException(String.format("В заголовке запроса на проведение операции '%s' " +
                    "с данными объекта не передан идентификатор пользователя, либо указан 0", operation));
        }
    }

    public void checkPaginationParameters(int from, int size) {
        if (from < 0) {
            throw new BadRequestParameterException("В параметре запроса указано неверное значение порядкового номера " +
                    "первого отображаемого элемента '" + from + "'. Значение данного параметра не должно быть меньше 0");
        }
        if (size <= 0) {
            throw new BadRequestParameterException("В параметре запроса указано неверное значение количества " +
                    "отображаемых элементов, равное '" + size + "'. Значение данного параметра не должно быть меньше, чем 1");
        }
    }

    public void checkUserEmail(String email) {
        String[] emailElements = email.split("@");
        if (!emailElements[1].contains(".")) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить пользователя. Причина - " +
                            "неправильный формат адреса электронной почты '%s': отсутствует точка в домене", email));
        }
    }

}