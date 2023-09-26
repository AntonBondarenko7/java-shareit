package ru.practicum.shareit.item.exception;

import ru.practicum.shareit.common.exception.NotSavedException;

public class ItemNotSavedException extends NotSavedException {

    public ItemNotSavedException() {
        super("Не удалось сохранить данные вещи");
    }

}
