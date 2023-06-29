package ru.practicum.shareit.item.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class Comment {
    @JsonProperty("id")
    long id;
    @JsonProperty("authorId")
    long authorId;
    @JsonProperty("authorName")
    String authorName;
    @JsonProperty("text")
    String text;
    @JsonProperty("created")
    LocalDateTime created;
    @JsonProperty("lastModified")
    LocalDateTime lastModified;

}