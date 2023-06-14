package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import ru.practicum.shareit.common.IdentificableObject;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

@Value
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Builder(toBuilder = true)
public class Item extends IdentificableObject {
    long ownerId;
    String name;
    String description;
    Boolean isAvailable;
    // Ниже поля, которые отсутствуют в задании и тестах, но которые, как мне кажется, должны быть в логике приложения
    float rent;
    float itemRating;
    URI itemPhotoUri;
    LocalDate postDate;
    Set<Long> requestsWithUseIds;

}