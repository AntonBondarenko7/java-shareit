package ru.practicum.shareit.item.exception;

import ru.practicum.shareit.exception.NotFoundException;

public class ItemOwnershipException extends NotFoundException {

    public ItemOwnershipException(Long userId, Long itemId) {
        super("Пользователь с id = " + userId + " не является владельцем вещи c id = " + itemId);
    }

}
