package ru.practicum.shareit_server.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ItemBooking {
    @JsonProperty("id")
    long id;
    @JsonProperty("bookerId")
    long bookerId;
    // Ниже поля, которые не проверяются Postman, но как мне кажется, они понадобятся по смыслу на фронтэнде
    @JsonProperty("status")
    String status;
    @JsonProperty("start")
    LocalDateTime start;
    @JsonProperty("end")
    LocalDateTime end;

}
