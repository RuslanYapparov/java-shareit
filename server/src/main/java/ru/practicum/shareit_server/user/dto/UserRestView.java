package ru.practicum.shareit_server.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import ru.practicum.shareit_server.user.UserAddress;

import java.net.URI;
import java.time.LocalDateTime;

@Value
@Builder
public class UserRestView {
    @JsonProperty("id")
    long id;
    @JsonProperty("name")
    String name;
    @JsonProperty("email")
    String email;
    // Ниже поля, которые отсутствуют в задании и тестах, но которые, как мне кажется, должны быть в логике приложения
    @JsonProperty("address")
    UserAddress address;
    @JsonProperty("telephoneNumber")
    String telephoneNumber;
    @JsonProperty("telephoneVisible")
    boolean telephoneVisible;
    @JsonProperty("avatarUri")
    URI avatarUri;
    @JsonProperty("created")
    LocalDateTime created;
    @JsonProperty("lastModified")
    LocalDateTime lastModified;
    @JsonProperty("userRating")
    float userRating;

}