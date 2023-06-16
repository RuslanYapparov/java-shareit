package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

@EqualsAndHashCode
@RequiredArgsConstructor
@Builder
public class ItemRestView {
    @JsonProperty("id")
    private final long id;
    @JsonProperty("ownerId")
    private final long ownerId;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("description")
    private final String description;
    @JsonProperty("available")
    private final boolean isAvailable;
    // Ниже поля, которые отсутствуют в задании и тестах, но которые, как мне кажется, должны быть в логике приложения
    @JsonProperty("rent")
    private final float rent;
    @JsonProperty("itemRating")
    private final float itemRating;
    @JsonProperty("itemPhotoUri")
    private final URI itemPhotoUri;
    @JsonProperty("postDate")
    private final LocalDate postDate;
    @JsonProperty("requestsWithUseIds")
    private final Set<Long> requestsWithUseIds;

}