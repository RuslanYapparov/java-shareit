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

@NoArgsConstructor
@Getter
@Setter
public class UserRestCommand {
    @PositiveOrZero
    private long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @Email
    private String email;
    // Ниже поля, которые отсутствуют в задании и тестах, но которые, как мне кажется, должны быть в логике приложения
    private Set<Long> userItemsIds;
    private UserAddressRestCommand address;
    private long telephoneNumber;
    private boolean isTelephoneVisible;
    private URI avatarUri;
    private LocalDate registrationDate;
    private float userRating;     // Наверное, пользователей (владельцев и арендаторов) имеет смысл оценивать другим

}