package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

@EqualsAndHashCode
@NoArgsConstructor
@Setter
public class ItemRestView {
    @JsonProperty("id")
    private long id;
    @JsonProperty("ownerId")
    private long ownerId;
    @JsonProperty("name")
    String name;
    @JsonProperty("description")
    String description;
    @JsonProperty("available")
    boolean isAvailable;
    // Ниже поля, которые отсутствуют в задании и тестах, но которые, как мне кажется, должны быть в логике приложения
    @JsonProperty("rent")
    float rent;
    @JsonProperty("itemRating")
    float itemRating;
    @JsonProperty("itemPhotoUri")
    URI itemPhotoUri;
    @JsonProperty("postDate")
    LocalDate postDate;
    @JsonProperty("requestsWithUseIds")
    Set<Long> requestsWithUseIds;

}