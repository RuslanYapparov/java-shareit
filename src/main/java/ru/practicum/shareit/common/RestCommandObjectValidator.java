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
        return UserRestCommand.builder()
                .id(checkLongField(type, "id", userCommand.getId()))
                .name(checkStringField(type, "name", userCommand.getName()))
                .email(checkStringField(type, "email", userCommand.getEmail()))
                .userItemsIds(checkSetFieldWithLongs(userCommand.getUserItemsIds()))
                .address(validate(userCommand.getAddress(), userCommand.getId()))
                .telephoneNumber(userCommand.getTelephoneNumber())
                .isTelephoneVisible(userCommand.isTelephoneVisible())
                .avatarUri(checkUriField(type, "avatar_uri", userCommand.getAvatarUri()))
                .registrationDate(checkLocalDate(type, "registration_date", userCommand.getRegistrationDate()))
                .userRating(userCommand.getUserRating())
                .build();
    }

    public ItemRestCommand validate(ItemRestCommand itemCommand) {
        String type = "вещь";
        if (itemCommand == null) {
            throw new BadRequestBodyException("Не переданы данные вещи для сохранения/обновления в Http-запросе. " +
                    "Если Вы видите данное сообщение, пожалуйста, сообщите разработчикам");
        }
        return ItemRestCommand.builder()
                .id(checkLongField(type, "id", itemCommand.getId()))
                .ownerId(checkLongField(type, "owner_id", itemCommand.getOwnerId()))
                .name(checkStringField(type, "name", itemCommand.getName()))
                .description(checkStringField(type, "description", itemCommand.getDescription()))
                .available(itemCommand.getAvailable())
                .rent(checkFloatField(type, "rent", itemCommand.getRent()))
                .itemRating(itemCommand.getItemRating())
                .itemPhotoUri(checkUriField(type, "photo_uri", itemCommand.getItemPhotoUri()))
                .itemPostDate(checkLocalDate(type, "post_date", itemCommand.getItemPostDate()))
                .requestsWithUseIds(checkSetFieldWithLongs(itemCommand.getRequestsWithUseIds()))
                .build();
    }

    private UserAddressRestCommand validate(UserAddressRestCommand addressCommand, long userId) {
        String type = "адрес пользователя";
        if (addressCommand == null) {
            return UserAddressRestCommand.builder()
                    .userId(userId)
                    .country(ShareItConstants.NOT_ASSIGNED)
                    .region(ShareItConstants.NOT_ASSIGNED)
                    .cityOrSettlement(ShareItConstants.NOT_ASSIGNED)
                    .cityDistrict(ShareItConstants.NOT_ASSIGNED)
                    .street(ShareItConstants.NOT_ASSIGNED)
                    .build();
        } else {
            if (userId != addressCommand.getUserId()) {
                throw new BadRequestBodyException("Невозможно сохранить пользователя - значение его " +
                        "идентификатора не совпадает со значением, указанном в адресе");
            }
            return UserAddressRestCommand.builder()
                    .userId(userId)
                    .country(checkStringField(type, "country", addressCommand.getCountry()))
                    .region(checkStringField(type, "region", addressCommand.getRegion()))
                    .cityOrSettlement(checkStringField(type, "city", addressCommand.getCityOrSettlement()))
                    .cityDistrict(checkStringField(type, "district", addressCommand.getCityDistrict()))
                    .street(checkStringField(type, "street", addressCommand.getStreet()))
                    .houseNumber((int) checkLongField(type, "house_number", addressCommand.getHouseNumber()))
                    .build();
        }
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