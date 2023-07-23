package ru.practicum.shareit_server.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Value
@Builder(toBuilder = true)
public class ItemRestCommand {
    @JsonProperty("requestId")
    long requestId;
    @JsonProperty("name")
    String name;
    @JsonProperty("description")
    String description;
    @JsonProperty("available")
    Boolean available;

}