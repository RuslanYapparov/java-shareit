package ru.practicum.shareit_gateway.exception;

public class BadRequestBodyException extends RuntimeException {

    public BadRequestBodyException(String message) {
        super(message);
    }

}