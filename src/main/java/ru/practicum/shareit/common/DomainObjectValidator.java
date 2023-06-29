package ru.practicum.shareit.common;

import ru.practicum.shareit.exception.BadRequestBodyException;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public interface DomainObjectValidator<T> {
    // T - тип данных объекта сервисного слоя (user, item, etc.)
    T validateAndAssignNullFields(T domainObject);

    default long checkLongField(String type, String fieldName, long value) {
        if (value < 0L) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить %s со значением параметра %s '%d'. " +
                    "Значение должно быть положительным числом", type, fieldName, value));
        }
        return value;
    }

    default float checkFloatField(String type, String fieldName, float value) {
        if (value < 0.0) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить %s со значением параметра %s '%f'. " +
                    "Значение должно быть положительным числом", type, fieldName, value));
        }
        return value;
    }

    default String checkStringField(String type, String fieldName, String value) {
        if (ShareItConstants.NOT_ASSIGNED.equals(value)) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить %s со значением параметра %s '%s'. " +
                    "Данное обозначение зарезервино системой", type, fieldName, value));
        }
        return (value == null) ? ShareItConstants.NOT_ASSIGNED : value;
    }

    default URI checkUriField(String type, String fieldName, URI value) {
        if (ShareItConstants.DEFAULT_URI.equals(value)) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить %s со значением параметра %s '%s'. " +
                    "Данное обозначение зарезервино системой", type, fieldName, value));
        }
        return (value == null) ? ShareItConstants.DEFAULT_URI : value;
    }

    default Set<Long> checkSetFieldWithLongs(Set<Long> value) {
        return (value == null) ? new HashSet<>() : value;
    }

}