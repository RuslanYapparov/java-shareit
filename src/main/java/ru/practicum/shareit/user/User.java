package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import ru.practicum.shareit.common.IdentificableObject;
import ru.practicum.shareit.user.address.UserAddress;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

@Value
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Builder(toBuilder = true)
public class User extends IdentificableObject {
    String name;
    String email;
    // Ниже поля, которые отсутствуют в задании и тестах, но которые, как мне кажется, должны быть в логике приложения
    Set<Long> userItemsIds;
    UserAddress address;
    long telephoneNumber;
    boolean isTelephoneVisible;
    URI avatarUri;
    LocalDate registrationDate;
    float userRating;   // Наверное, пользователей (владельцев и арендаторов) имеет смысл оценивать другим пользовтелям

}