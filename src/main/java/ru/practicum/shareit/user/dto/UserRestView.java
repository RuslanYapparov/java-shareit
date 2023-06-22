package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import ru.practicum.shareit.user.address.dto.UserAddressRestView;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

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
    @JsonProperty("userItemsIds")
    Set<Long> userItemsIds;
    @JsonProperty("address")
    UserAddressRestView address;
    @JsonProperty("telephoneNumber")
    String telephoneNumber;
    @JsonProperty("telephoneVisible")
    boolean telephoneVisible;
    @JsonProperty("avatarUri")
    URI avatarUri;
    @JsonProperty("registrationDate")
    LocalDate registrationDate;
    @JsonProperty("userRating")
    float userRating;      // Наверное, пользователей (владельцев и арендаторов) имеет смысл оценивать другим

}