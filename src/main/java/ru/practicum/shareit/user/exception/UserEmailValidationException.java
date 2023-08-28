package ru.practicum.shareit.user.exception;

public class UserEmailValidationException extends RuntimeException {

    public UserEmailValidationException() {
        super("Пользователь с таким email уже существует");
    }
}
