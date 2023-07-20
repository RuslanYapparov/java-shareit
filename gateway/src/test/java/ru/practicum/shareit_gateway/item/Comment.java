package ru.practicum.shareit_gateway.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
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