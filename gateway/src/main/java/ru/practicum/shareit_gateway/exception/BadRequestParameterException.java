package ru.practicum.shareit_gateway.exception;

public class BadRequestParameterException extends RuntimeException {

    public BadRequestParameterException(String message) {
        super(message);
    }

}