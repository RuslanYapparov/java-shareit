package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import ru.practicum.shareit.user.address.dto.UserAddressRestView;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

@RequiredArgsConstructor
@Builder
public class UserRestView {
    @JsonProperty("id")
    private final long id;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("email")
    private final String email;
    // Ниже поля, которые отсутствуют в задании и тестах, но которые, как мне кажется, должны быть в логике приложения
    @JsonProperty("userItemsIds")
    private final Set<Long> userItemsIds;
    @JsonProperty("address")
    private final UserAddressRestView address;
    @JsonProperty("telephoneNumber")
    private final long telephoneNumber;
    @JsonProperty("isTelephoneVisible")
    private final boolean isTelephoneVisible;
    @JsonProperty("avatarUri")
    private final URI avatarUri;
    @JsonProperty("registrationDate")
    private final LocalDate registrationDate;
    @JsonProperty("userRating")
    private final float userRating;      // Наверное, пользователей (владельцев и арендаторов) имеет смысл оценивать другим

}