package ru.practicum.shareit_server.item.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@Setter
@Getter
public class CommentRestCommand {
    @JsonProperty("text")
    private String text;

}