package ru.practicum.shareit_gateway.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Setter
@Getter
public class CommentRestCommand {
    @NotNull
    @NotBlank
    @Size(max = 2000)
    private String text;

}