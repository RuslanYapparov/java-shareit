package ru.practicum.shareit.common;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.BadRequestBodyException;
import ru.practicum.shareit.item.dto.ItemRestCommand;
import ru.practicum.shareit.user.address.dto.UserAddressRestCommand;
import ru.practicum.shareit.user.dto.UserRestCommand;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
public class RestCommandObjectValidator {

    public UserRestCommand validate(UserRestCommand userCommand) {
        String type = "пользователя";
        if (userCommand == null) {
            throw new BadRequestBodyException("Не переданы данные пользователя для сохранения/обновления в " +
                    "Http-запросе. Если Вы видите данное сообщение, пожалуйста, сообщите разработчикам");
        }
        userCommand.setId(checkLongField(type, "id", userCommand.getId()));
        userCommand.setName(checkStringField(type, "name", userCommand.getName()));
        userCommand.setEmail(checkStringField(type, "email", userCommand.getEmail()));
        userCommand.setUserItemsIds(checkSetFieldWithLongs(userCommand.getUserItemsIds()));
        userCommand.setAddress(validate(userCommand.getAddress(), userCommand.getId()));
        userCommand.setAvatarUri(checkUriField(type, "avatar_uri", userCommand.getAvatarUri()));
        userCommand.setRegistrationDate(checkLocalDate(
                type, "registration_date", userCommand.getRegistrationDate()));
        return userCommand;
    }

    public ItemRestCommand validate(ItemRestCommand itemCommand) {
        String type = "вещь";
        if (itemCommand == null) {
            throw new BadRequestBodyException("Не переданы данные вещи для сохранения/обновления в Http-запросе. Если Вы " +
                    "видите данное сообщение, пожалуйста, сообщите разработчикам");
        }
        itemCommand.setId(checkLongField(type, "id", itemCommand.getId()));
        itemCommand.setOwnerId(checkLongField(type, "owner_id", itemCommand.getOwnerId()));
        itemCommand.setName(checkStringField(type, "name", itemCommand.getName()));
        itemCommand.setDescription(checkStringField(type, "description", itemCommand.getDescription()));
        itemCommand.setAvailable(itemCommand.getAvailable());
        itemCommand.setRent(checkFloatField(type, "rent", itemCommand.getRent()));
        itemCommand.setItemRating(itemCommand.getItemRating());
        itemCommand.setItemPhotoUri(checkUriField(type, "photo_uri", itemCommand.getItemPhotoUri()));
        itemCommand.setItemPostDate(checkLocalDate(type, "post_date", itemCommand.getItemPostDate()));
        itemCommand.setRequestsWithUseIds(checkSetFieldWithLongs(itemCommand.getRequestsWithUseIds()));
        return itemCommand;
    }

    private UserAddressRestCommand validate(UserAddressRestCommand addressCommand, long userId) {
        String type = "адрес пользователя";
        if (addressCommand == null) {
            addressCommand = new UserAddressRestCommand();
            addressCommand.setUserId(userId);
            addressCommand.setCountry(ShareItConstants.NOT_ASSIGNED);
            addressCommand.setRegion(ShareItConstants.NOT_ASSIGNED);
            addressCommand.setCityDistrict(ShareItConstants.NOT_ASSIGNED);
            addressCommand.setCityDistrict(ShareItConstants.NOT_ASSIGNED);
            addressCommand.setStreet(ShareItConstants.NOT_ASSIGNED);
        } else {
            if (userId != addressCommand.getUserId()) {
                throw new BadRequestBodyException("Невозможно сохранить пользователя - значение его " +
                        "идентификатора не совпадает со значением, указанном в адресе");
            }
            addressCommand.setCountry(checkStringField(type, "country", addressCommand.getCountry()));
            addressCommand.setRegion(checkStringField(type, "region", addressCommand.getRegion()));
            addressCommand.setCityDistrict(checkStringField(
                    type, "city", addressCommand.getCityOrSettlement()));
            addressCommand.setCityDistrict(checkStringField(
                    type, "district", addressCommand.getCityDistrict()));
            addressCommand.setStreet(checkStringField(type, "street", addressCommand.getStreet()));
            addressCommand.setHouseNumber((int)
                    checkLongField(type, "house_number", addressCommand.getHouseNumber()));
        }
        return addressCommand;
    }

    private long checkLongField(String type, String fieldName, long value) {
        if (value < 0) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить %s со значением параметра %s '%d'. " +
                    "Значение должно быть положительным числом", type, fieldName, value));
        }
        return value;
    }

    private float checkFloatField(String type, String fieldName, float value) {
        if (value < 0) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить %s со значением параметра %s '%f'. " +
                    "Значение должно быть положительным числом", type, fieldName, value));
        }
        return value;
    }

    private String checkStringField(String type, String fieldName, String value) {
        if (ShareItConstants.NOT_ASSIGNED.equals(value)) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить %s со значением параметра %s '%s'. " +
                    "Данное обозначение зарезервино системой", type, fieldName, value));
        }
        return (value == null) ? ShareItConstants.NOT_ASSIGNED : value;
    }

    private LocalDate checkLocalDate(String type, String fieldName, LocalDate value) {
        if (ShareItConstants.DEFAULT_DATE.equals(value)) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить %s со значением параметра %s '%s'. " +
                     "Данное обозначение зарезервино системой",
                    type, fieldName, value.format(ShareItConstants.DATE_FORMATTER)));
        }
        return (value == null) ? ShareItConstants.DEFAULT_DATE : value;
    }

    private URI checkUriField(String type, String fieldName, URI value) {
        if (ShareItConstants.DEFAULT_URI.equals(value)) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить %s со значением параметра %s '%s'. " +
                            "Данное обозначение зарезервино системой", type, fieldName, value));
        }
        return (value == null) ? ShareItConstants.DEFAULT_URI : value;
    }

    private Set<Long> checkSetFieldWithLongs(Set<Long> value) {
        return (value == null) ? new HashSet<>() : value;
    }

}