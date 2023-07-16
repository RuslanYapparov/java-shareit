package ru.practicum.shareit.item.dto;

import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Value
@Builder(toBuilder = true)
public class ItemRestCommand {
    @PositiveOrZero
    long requestId;
    @NotNull
    @NotBlank
    @Size(max = 100)
    String name;
    @NotNull
    @NotBlank
    @Size(max = 1000)
    String description;
    @NotNull
    Boolean available;

}