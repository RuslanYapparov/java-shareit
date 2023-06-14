package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ru.practicum.shareit.user.address.dto.UserAddressRestView;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode
@NoArgsConstructor
@Setter
public class UserRestView {
    @JsonProperty("id")
    private long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("email")
    private String email;
    // Ниже поля, которые отсутствуют в задании и тестах, но которые, как мне кажется, должны быть в логике приложения
    @JsonProperty("userItemsIds")
    private Set<Long> userItemsIds = new HashSet<>();
    @JsonProperty("address")
    private UserAddressRestView address;
    @JsonProperty("telephoneNumber")
    private long telephoneNumber;
    @JsonProperty("isTelephoneVisible")
    private boolean isTelephoneVisible;
    @JsonProperty("avatarUri")
    private URI avatarUri;
    @JsonProperty("registrationDate")
    private LocalDate registrationDate;
    @JsonProperty("userRating")
    private float userRating;      // Наверное, пользователей (владельцев и арендаторов) имеет смысл оценивать другим

}