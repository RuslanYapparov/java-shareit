package ru.practicum.shareit.exception;

public class BadRequestBodyException extends RuntimeException {

    public BadRequestBodyException(String message) {
        super(message);
    }

}