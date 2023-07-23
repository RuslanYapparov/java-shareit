package ru.practicum.shareit_gateway.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class BookingRestCommand {
    @Positive
    @JsonProperty("itemId")
    long itemId;
    @NotNull
    @FutureOrPresent
    @JsonProperty("start")
    LocalDateTime start;
    @NotNull
    @Future
    @JsonProperty("end")
    LocalDateTime end;

}