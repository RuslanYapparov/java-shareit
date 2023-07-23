package ru.practicum.shareit_gateway.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("text")
    private String text;

}