package ru.practicum.shareit.exception;

public class ObjectAlreadyExistsException extends RuntimeException {

    public ObjectAlreadyExistsException(String message) {
        super(message);
    }

}