package ru.practicum.shareit.user.exception;

import ru.practicum.shareit.exception.NotSavedException;

public class UserNotSavedException extends NotSavedException {

    public UserNotSavedException() {
        super("Не удалось сохранить данные пользователя");
    }

}
