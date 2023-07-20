package ru.practicum.shareit_gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ShareItExceptionHandler extends ResponseEntityExceptionHandler {

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
        log.warn("Попытка получения объектов 'booking' с указанием неверного статуса");
        return new ResponseEntity<>(exception.getExceptionRestView(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalLogicException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseView handleInternalLogicException(InternalLogicException exception) {
        log.warn(exception.getMessage());
        return new ErrorResponseView(HttpStatus.INTERNAL_SERVER_ERROR.value(), "InternalLogicException",
                exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponseView> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException exception) {
        ErrorResponseView error = new ErrorResponseView(HttpStatus.BAD_REQUEST.value(),
                "MethodArgumentTypeMismatchException",
                String.format("The parameter '%s' of value '%s' could not be converted to type '%s'. Cause: %s",
                        exception.getName(),
                        exception.getValue(),
                        exception.getRequiredType().getSimpleName(),
                        exception.getMessage()));
        log.warn(error.getException() + error.getDebugMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseView> handleConstraintViolationException(
            ConstraintViolationException exception) {
        String message = exception.getMessage();
        ErrorResponseView error;
        log.warn(exception.toString());
        error = new ErrorResponseView(HttpStatus.BAD_REQUEST.value(), "ConstraintViolationException", message);
        return new ResponseEntity<>(error, HttpStatus.valueOf(error.getStatusCode()));
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  @NonNull HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  @NonNull WebRequest request) {
        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        ErrorResponseView error = new ErrorResponseView(status.value(),"MethodArgumentNotValidException",
                exception.getMessage());
        error.setErrors(errors);
        log.warn(error.getException() + error.getErrors());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseView handleAnotherUnhandledException(RuntimeException exception) {
        log.warn(exception.getClass().toString() + ": " + exception.getMessage());
        return new ErrorResponseView(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getClass().toString(),
                exception.getMessage());
    }

}