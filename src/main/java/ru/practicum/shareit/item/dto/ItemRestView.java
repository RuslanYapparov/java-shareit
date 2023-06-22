package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

@Value
@Builder
public class ItemRestView {
    @JsonProperty("id")
    long id;
    @JsonProperty("ownerId")
    long ownerId;
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