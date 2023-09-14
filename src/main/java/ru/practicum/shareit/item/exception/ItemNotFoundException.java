package ru.practicum.shareit.item.exception;

import ru.practicum.shareit.common.NotFoundException;

public class ItemNotFoundException extends NotFoundException {

    public ItemNotFoundException(Long itemId) {
        super("Вещь с идентификатором " + itemId +  " не найдена.");
    }

}
