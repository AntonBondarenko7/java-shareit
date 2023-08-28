package ru.practicum.shareit.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.user.exception.UserEmailValidationException;
import ru.practicum.shareit.user.exception.UserNotFoundException;


@RestControllerAdvice
@Slf4j
public class AdviceController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleUserEmailValidationException(final UserEmailValidationException e) {
        log.debug("Ошибка валидации: 409 Conflict {}", e.getMessage(), e);
        return "Ошибка валидации: " + e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserUserNotFoundException(final UserNotFoundException e) {
        log.debug("Ошибка: 404 NOT_FOUND {}", e.getMessage(), e);
        return "Ошибка: " + e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(final MethodArgumentNotValidException e) {
        log.debug("Ошибка валидации: 400 BAD_REQUEST {}", e.getMessage(), e);
        return "Ошибка валидации: " + e.getMessage();
    }
}
