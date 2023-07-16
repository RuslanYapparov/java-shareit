package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import ru.practicum.shareit.common.DomainObjectValidator;
import ru.practicum.shareit.common.ShareItConstants;
import ru.practicum.shareit.exception.BadRequestBodyException;

@Component
public class UserValidator implements DomainObjectValidator<User> {

    @Override
    public User validateAndAssignNullFields(User user) {
        String type = "пользователя";
        if (user == null) {
            throw new BadRequestBodyException("Не переданы данные пользователя для сохранения/обновления в " +
                    "Http-запросе. Если Вы видите данное сообщение, пожалуйста, сообщите разработчикам");
        }
        return User.builder()
                .id(checkLongField(type, "id", user.getId()))
                .name(checkStringField(type, "name", user.getName()))
                .email(checkStringField(type, "email", user.getEmail()))
                .address(validate(user.getAddress()))
                .telephoneNumber(checkStringField(type, "telephone_number", user.getTelephoneNumber()))
                .telephoneVisible(user.isTelephoneVisible())
                .avatarUri(checkUriField(type, "avatar_uri", user.getAvatarUri()))
                .userRating(user.getUserRating())
                .build();
    }

    private UserAddress validate(UserAddress address) {
        String type = "адрес пользователя";
        if (address == null) {
            return UserAddress.builder()
                    .country(ShareItConstants.NOT_ASSIGNED)
                    .region(ShareItConstants.NOT_ASSIGNED)
                    .cityOrSettlement(ShareItConstants.NOT_ASSIGNED)
                    .cityDistrict(ShareItConstants.NOT_ASSIGNED)
                    .street(ShareItConstants.NOT_ASSIGNED)
                    .build();
        } else {
            return UserAddress.builder()
                    .country(checkStringField(type, "country", address.getCountry()))
                    .region(checkStringField(type, "region", address.getRegion()))
                    .cityOrSettlement(checkStringField(type, "city", address.getCityOrSettlement()))
                    .cityDistrict(checkStringField(type, "district", address.getCityDistrict()))
                    .street(checkStringField(type, "street", address.getStreet()))
                    .houseNumber((int) checkLongField(type, "house_number", address.getHouseNumber()))
                    .build();
        }
    }

}