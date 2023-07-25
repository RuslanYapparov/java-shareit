package ru.practicum.shareit_server.exception;

public class BadRequestBodyException extends RuntimeException {

    public BadRequestBodyException(String message) {
        super(message);
    }

}