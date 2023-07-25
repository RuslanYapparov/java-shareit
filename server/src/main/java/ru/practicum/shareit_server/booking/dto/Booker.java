package ru.practicum.shareit_server.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class Booker {
    @JsonProperty("id")
    long id;

}