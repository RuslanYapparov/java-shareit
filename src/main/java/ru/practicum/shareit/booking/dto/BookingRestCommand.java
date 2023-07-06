package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Value;
import javax.validation.constraints.*;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class BookingRestCommand {
    @PositiveOrZero
    long bookerId;
    @Positive
    long itemId;
    @NotNull
    @FutureOrPresent
    LocalDateTime start;
    @NotNull
    @Future
    LocalDateTime end;

}