package ru.practicum.shareit_server.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
public class ErrorResponseView {
    @JsonProperty("statusCode")
    private final int statusCode;
    @JsonProperty("exception")
    private final String exception;
    @JsonProperty("debugMessage")
    private final String debugMessage;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Setter
    private List<String> errors;

    public ErrorResponseView(int statusCode, String exception, String debugMessage) {
        this.statusCode = statusCode;
        this.exception = exception;
        this.debugMessage = debugMessage;
    }

}