package ru.practicum.shareit.user.address.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import ru.practicum.shareit.exception.BadRequestBodyException;
import ru.practicum.shareit.exception.BadRequestHeaderException;
import ru.practicum.shareit.common.Receivable;
import ru.practicum.shareit.common.ShareItConstants;

@NoArgsConstructor
@Getter
public class UserAddressRestCommand implements Receivable<UserAddressRestCommand> {
    long userId;
    String country;
    String region;
    String cityOrSettlement;
    String cityDistrict;
    String street;
    int houseNumber;

    @Override
    public UserAddressRestCommand insertDefaultValuesInNullFields() throws BadRequestHeaderException, BadRequestBodyException {
        checkFieldsForDefaultAndIncorrectValues();
        country = (country == null) ? ShareItConstants.NOT_ASSIGNED : country;
        region = (region == null) ? ShareItConstants.NOT_ASSIGNED : region;
        cityOrSettlement = (cityOrSettlement == null) ? ShareItConstants.NOT_ASSIGNED : cityOrSettlement;
        cityDistrict = (cityDistrict == null) ? ShareItConstants.NOT_ASSIGNED : cityDistrict;
        street = (street == null) ? ShareItConstants.NOT_ASSIGNED : street;
        return this;
    }

    @Override
    public void checkFieldsForDefaultAndIncorrectValues() throws BadRequestBodyException {
        if (userId < 0) {
            throw new BadRequestBodyException("Указан неверный идентификатор пользователя в его местонахождении - " +
                    "идентификатор должен быть положительным числом");
        }
        if (country != null && country.equals(ShareItConstants.NOT_ASSIGNED)) {
            throw new BadRequestBodyException(String.format("Указано недопустимое название страны '%s' в " +
                    "местонахождении пользователя - Данное обозначение зарезервино системой", country));
        }
        if (region != null && region.equals(ShareItConstants.NOT_ASSIGNED)) {
            throw new BadRequestBodyException(String.format("Указано недопустимое название региона страны '%s' в " +
                    "местонахождении пользователя - Данное обозначение зарезервино системой", region));
        }
        if (cityOrSettlement != null && cityOrSettlement.equals(ShareItConstants.NOT_ASSIGNED)) {
            throw new BadRequestBodyException(String.format("Указано недопустимое название города '%s' в " +
                    "местонахождении пользователя - Данное обозначение зарезервино системой", cityOrSettlement));
        }
        if (cityDistrict != null && cityDistrict.equals(ShareItConstants.NOT_ASSIGNED)) {
            throw new BadRequestBodyException(String.format("Указано недопустимое название района города '%s' в " +
                    "местонахождении пользователя - Данное обозначение зарезервино системой", cityDistrict));
        }
        if (street != null && street.equals(ShareItConstants.NOT_ASSIGNED)) {
            throw new BadRequestBodyException(String.format("Указано недопустимое название улицы '%s' в " +
                    "местонахождении пользователя - Данное обозначение зарезервино системой", street));
        }
        if (houseNumber < 0) {
            throw new BadRequestBodyException("Указан неверный номер дома пользователя в его местонахождении - " +
                    "номер дома должен быть положительным числом");
        }
    }

}