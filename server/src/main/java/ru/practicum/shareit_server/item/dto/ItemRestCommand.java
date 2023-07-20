package ru.practicum.shareit_server.item.dto;

import lombok.*;

@Value
@Builder(toBuilder = true)
public class ItemRestCommand {
    long requestId;
    String name;
    String description;
    Boolean available;

}