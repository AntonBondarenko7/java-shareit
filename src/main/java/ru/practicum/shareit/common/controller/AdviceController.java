package ru.practicum.shareit.common.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.common.exception.ErrorResponse;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.NotSavedException;
import ru.practicum.shareit.common.exception.ValidationException;

import java.util.Objects;


@RestControllerAdvice
@Slf4j
public class AdviceController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleNotSavedException(final NotSavedException e) {
        log.debug("Ошибка: 409 CONFLICT {}", e.getMessage(), e);
        return "Ошибка: " + e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(final NotFoundException e) {
        log.debug("Ошибка: 404 NOT_FOUND {}", e.getMessage(), e);
        return "Ошибка: " + e.getMessage();
    }

    @ExceptionHandler({ValidationException.class, HttpMessageNotReadableException.class,
            MissingRequestHeaderException.class, MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(final Exception e) {
        String message;
        if (e instanceof MethodArgumentNotValidException) {
            message = Objects.requireNonNull(Objects.requireNonNull(((MethodArgumentNotValidException) e)
                    .getFieldError()).getDefaultMessage());
        } else if (e instanceof MethodArgumentTypeMismatchException
                && ((MethodArgumentTypeMismatchException) e).getName().equals("state")) {
            message = "Unknown state: " + ((MethodArgumentTypeMismatchException) e).getValue();
        } else {
            message = e.getMessage();
        }
        log.debug("Ошибка валидации: 400 BAD_REQUEST {}", message, e);
        return new ErrorResponse(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllUnhandledExceptions(final Throwable e) {
        log.debug("Ошибка: 500 INTERNAL_SERVER_ERROR {}", e.getMessage(), e);
        return "Произошла непредвиденная ошибка.";
    }

}
