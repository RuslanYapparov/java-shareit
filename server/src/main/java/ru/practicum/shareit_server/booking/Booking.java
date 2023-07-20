package ru.practicum.shareit_server.booking;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class Booking {
    long id;
    long bookerId;
    long itemId;
    long itemOwnerId;
    String itemName;
    BookingStatus bookingStatus;
    LocalDateTime start;
    LocalDateTime end;
    LocalDateTime created;
    LocalDateTime lastModified;

}