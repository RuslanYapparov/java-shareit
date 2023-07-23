package ru.practicum.shareit_gateway.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder(toBuilder = true)
public class ItemRestView {
    @JsonProperty("id")
    long id;
    @JsonProperty("ownerId")
    long ownerId;
    @JsonProperty("requestId")
    long requestId;
    @JsonProperty("name")
    String name;
    @JsonProperty("description")
    String description;
    @JsonProperty("available")
    boolean available;
    @JsonProperty("nextBooking")
    ItemBooking nextBooking;
    @JsonProperty("lastBooking")
    ItemBooking lastBooking;
    @JsonProperty("comments")
    List<Comment> comments;
    // Ниже поля, которые отсутствуют в задании и тестах, но которые, как мне кажется, должны быть в логике приложения
    @JsonProperty("rent")
    float rent;
    @JsonProperty("itemRating")
    float itemRating;
    @JsonProperty("itemPhotoUri")
    URI itemPhotoUri;
    @JsonProperty("created")
    LocalDateTime created;
    @JsonProperty("lastModified")
    LocalDateTime lastModified;

}