package ru.practicum.shareit.request.exception;

import ru.practicum.shareit.exception.NotSavedException;

public class ItemRequestNotSavedException extends NotSavedException {

    public ItemRequestNotSavedException() {
        super("Не удалось сохранить данные запроса");
    }

}
