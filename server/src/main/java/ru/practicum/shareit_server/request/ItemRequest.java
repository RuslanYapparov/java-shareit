package ru.practicum.shareit_server.request;

import lombok.Builder;
import lombok.Value;

import ru.practicum.shareit_server.request.dto.RequestedItem;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder(toBuilder = true)
public class ItemRequest {
    long id;
    long requesterId;
    String description;
    LocalDateTime created;
    LocalDateTime lastModified;
    List<RequestedItem> items;

}