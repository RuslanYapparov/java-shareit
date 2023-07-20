package ru.practicum.shareit_gateway.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UnsupportedStateException extends RuntimeException {
    public ExceptionRestView exceptionRestView;

    public UnsupportedStateException(String message) {
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