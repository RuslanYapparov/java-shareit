package ru.practicum.shareit_gateway.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class BookingRestView {
    @JsonProperty("id")
    long id;
    @JsonProperty("status")
    String status;
    @JsonProperty("start")
    LocalDateTime start;
    @JsonProperty("end")
    LocalDateTime end;
    @JsonProperty("booker")
    Booker booker;
    @JsonProperty("item")
    BookedItem item;
    @JsonProperty("created")
    LocalDateTime created;
    @JsonProperty("lastModified")
    LocalDateTime lastModified;

}