package ru.practicum.shareit_gateway.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder(toBuilder = true)
public class ItemRequestRestView {
    @JsonProperty("id")
    long id;
    @JsonProperty("requesterId")
    long requesterId;
    @JsonProperty("description")
    String description;
    @JsonProperty("created")
    LocalDateTime created;
    @JsonProperty("lastModified")
    LocalDateTime lastModified;
    @JsonProperty("items")
    List<RequestedItem> items;

}