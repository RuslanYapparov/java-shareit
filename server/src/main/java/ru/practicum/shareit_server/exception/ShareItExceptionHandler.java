package ru.practicum.shareit_server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;


@RestControllerAdvice
@Slf4j
public class ShareItExceptionHandler {

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseView handleObjectNotFoundInStorageException(ObjectNotFoundException exception) {
        log.warn(exception.getMessage());
        return new ErrorResponseView(HttpStatus.NOT_FOUND.value(), "ObjectNotFoundException",
                exception.getMessage());
    }

    @ExceptionHandler(ObjectAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseView handleObjectAlreadyExistsException(ObjectAlreadyExistsException exception) {
        log.warn(exception.getMessage());
        return new ErrorResponseView(HttpStatus.CONFLICT.value(), "ObjectAlreadyExistsException",
                exception.getMessage());
    }

    @ExceptionHandler(BadRequestBodyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseView handleBadRequestBodyException(BadRequestBodyException exception) {
        log.warn(exception.getMessage());
        return new ErrorResponseView(HttpStatus.BAD_REQUEST.value(), "BadRequestBodyException",
                exception.getMessage());
    }

    @ExceptionHandler(BadRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseView handleBadRequestParameterException(BadRequestParameterException exception) {
        log.warn(exception.getMessage());
        return new ErrorResponseView(HttpStatus.BAD_REQUEST.value(), "BadRequestParameterException",
                exception.getMessage());
    }

    @ExceptionHandler(BadRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseView handleBadRequestHeaderException(BadRequestHeaderException exception) {
        log.warn(exception.getMessage());
        return new ErrorResponseView(HttpStatus.BAD_REQUEST.value(), "BadRequestHeaderException",
                exception.getMessage());
    }



    @ExceptionHandler(UnsupportedStateException.class)
    public ResponseEntity<Object> handleUnsupportedStatusException(UnsupportedStateException exception) {
        log.warn("Попытка получения объектов 'ru.practicum.shareit.booking' с указанием неверного статуса");
        return new ResponseEntity<>(exception.getExceptionRestView(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalLogicException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseView handleInternalLogicException(InternalLogicException exception) {
        log.warn(exception.getMessage());
        return new ErrorResponseView(HttpStatus.INTERNAL_SERVER_ERROR.value(), "InternalLogicException",
                exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseView handleAnotherUnhandledException(RuntimeException exception) {
        log.warn(exception.getClass().toString() + ": " + exception.getMessage());
        return new ErrorResponseView(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getClass().toString(),
                exception.getMessage());
    }

}