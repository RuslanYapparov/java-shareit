package ru.practicum.shareit.exception;

public class BadRequestHeaderException extends RuntimeException {

    public BadRequestHeaderException(String message) {
        super(message);
    }

}