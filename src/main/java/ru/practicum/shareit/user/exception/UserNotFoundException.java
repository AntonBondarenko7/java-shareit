package ru.practicum.shareit.user.exception;

import ru.practicum.shareit.common.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(Long userId) {
        super("Пользователь с идентификатором " + userId + " не найден.");
    }
}
