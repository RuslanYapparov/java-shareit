package ru.practicum.shareit_server.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class BookingRestCommand {
    @JsonProperty("itemId")
    long itemId;
    @JsonProperty("start")
    LocalDateTime start;
    @JsonProperty("end")
    LocalDateTime end;

}