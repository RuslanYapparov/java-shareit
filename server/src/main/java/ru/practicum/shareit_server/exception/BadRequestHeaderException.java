package ru.practicum.shareit_server.exception;

public class BadRequestHeaderException extends RuntimeException {

    public BadRequestHeaderException(String message) {
        super(message);
    }

}