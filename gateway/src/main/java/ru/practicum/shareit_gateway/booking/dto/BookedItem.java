package ru.practicum.shareit_gateway.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class BookedItem {
    @JsonProperty("id")
    long id;
    @JsonProperty("name")
    String name;

}