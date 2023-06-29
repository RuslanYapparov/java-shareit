package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UnsupportedStatusException extends RuntimeException {
    public ExceptionRestView exceptionRestView;

    public UnsupportedStatusException(String message) {
        super(message);
        this.exceptionRestView = new ExceptionRestView(message);
    }

    public ExceptionRestView getExceptionRestView() {
        return exceptionRestView;
    }

    private static class ExceptionRestView {
        @JsonProperty("error")
        String errorMessage;

        private ExceptionRestView(String errorMessage) {
            this.errorMessage = errorMessage;
        }

    }

}