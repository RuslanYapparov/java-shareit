package ru.practicum.shareit_server.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Value
@Builder(toBuilder = true)
public class UserRestCommand {
    @JsonProperty("name")
    String name;
    @JsonProperty("email")
    String email;

}