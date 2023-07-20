package ru.practicum.shareit_server.booking.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class BookingRestCommand {
    long itemId;
    LocalDateTime start;
    LocalDateTime end;

}