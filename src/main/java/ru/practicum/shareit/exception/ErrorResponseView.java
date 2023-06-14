package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
public class ErrorResponseView {
    private final int statusCode;
    private final String exception;
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