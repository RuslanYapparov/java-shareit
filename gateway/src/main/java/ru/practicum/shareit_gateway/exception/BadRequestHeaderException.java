package ru.practicum.shareit_gateway.exception;

public class BadRequestHeaderException extends RuntimeException {

    public BadRequestHeaderException(String message) {
        super(message);
    }

}