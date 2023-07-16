package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Value;

import ru.practicum.shareit.item.comment.Comment;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder(toBuilder = true)
public class Item {
    long id;
    long ownerId;
    long requestId;
    String name;
    String description;
    Boolean available;
    ItemBooking nextBooking;
    ItemBooking lastBooking;
    List<Comment> comments;
    // Ниже поля, которые отсутствуют в задании и тестах, но которые, как мне кажется, должны быть в логике приложения
    float rent;
    float itemRating;
    URI itemPhotoUri;
    LocalDateTime created;
    LocalDateTime lastModified;

}