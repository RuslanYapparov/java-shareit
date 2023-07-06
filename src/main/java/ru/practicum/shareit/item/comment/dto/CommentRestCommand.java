package ru.practicum.shareit.item.comment.dto;

import lombok.*;
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