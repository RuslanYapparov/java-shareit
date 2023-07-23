package ru.practicum.shareit_gateway.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Value
@Builder(toBuilder = true)
public class ItemRestCommand {
    @PositiveOrZero
    @JsonProperty("requestId")
    long requestId;
    @NotNull
    @NotBlank
    @Size(max = 100)
    @JsonProperty("name")
    String name;
    @NotNull
    @NotBlank
    @Size(max = 1000)
    @JsonProperty("description")
    String description;
    @NotNull
    @JsonProperty("available")
    Boolean available;

}