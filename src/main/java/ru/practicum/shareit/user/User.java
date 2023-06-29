package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Value;

import java.net.URI;
import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class User {
    long id;
    String name;
    String email;
    // Ниже поля, которые отсутствуют в задании и тестах, но которые, как мне кажется, должны быть в логике приложения
    UserAddress address;
    String telephoneNumber;
    boolean isTelephoneVisible;
    URI avatarUri;
    float userRating;   // Наверное, пользователей (владельцев и арендаторов) имеет смысл оценивать другим пользовтелям
    LocalDateTime created;
    LocalDateTime lastModified;

}