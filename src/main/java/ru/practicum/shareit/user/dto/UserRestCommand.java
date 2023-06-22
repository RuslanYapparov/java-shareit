package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import ru.practicum.shareit.user.address.dto.UserAddressRestCommand;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class UserRestCommand {
    @PositiveOrZero
    long id;
    @NotNull
    @NotBlank
    String name;
    @NotNull
    @Email
    String email;
    // Ниже поля, которые отсутствуют в задании и тестах, но которые, как мне кажется, должны быть в логике приложения
    Set<Long> userItemsIds;
    UserAddressRestCommand address;
    String telephoneNumber;
    boolean isTelephoneVisible;
    URI avatarUri;
    LocalDate registrationDate;
    float userRating;     // Наверное, пользователей (владельцев и арендаторов) имеет смысл оценивать другим

}