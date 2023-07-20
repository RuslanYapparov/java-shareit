package ru.practicum.shareit_server.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ItemRequestRestCommand {
    @JsonProperty("description")
    String description;

}