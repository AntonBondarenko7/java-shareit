package ru.practicum.shareit.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.user.exception.UserEmailValidationException;


@RestControllerAdvice
@Slf4j
public class AdviceController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleUserEmailValidationException(final UserEmailValidationException e) {
        log.debug("Ошибка: 409 CONFLICT {}", e.getMessage(), e);
        return "Ошибка: " + e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(final NotFoundException e) {
        log.debug("Ошибка: 404 NOT_FOUND {}", e.getMessage(), e);
        return "Ошибка: " + e.getMessage();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class,
            HttpMessageNotReadableException.class, MissingRequestHeaderException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(final RuntimeException e) {
        log.debug("Ошибка валидации: 400 BAD_REQUEST {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllUnhandledExceptions(final Throwable e) {
        log.debug("Ошибка: 500 INTERNAL_SERVER_ERROR {}", e.getMessage(), e);
        return "Ошибка: " + e.getMessage();
    }

}
