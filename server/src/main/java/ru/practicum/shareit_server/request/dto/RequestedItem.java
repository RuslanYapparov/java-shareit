package ru.practicum.shareit_server.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class RequestedItem {
    @JsonProperty("id")
    long id;
    @JsonProperty("requestId")
    long requestId;
    @JsonProperty("name")
    String name;
    @JsonProperty("description")
    String description;
    @JsonProperty("available")
    boolean available;

}