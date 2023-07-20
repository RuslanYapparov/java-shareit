package ru.practicum.shareit_server.exception;

public class BadRequestParameterException extends RuntimeException {

    public BadRequestParameterException(String message) {
        super(message);
    }

}