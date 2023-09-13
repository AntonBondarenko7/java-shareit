package ru.practicum.shareit.item.exception;

public class ItemOwnershipException extends RuntimeException {

    public ItemOwnershipException(Long userId, Long itemId) {
        super("Пользователь с id = " + userId + " не является владельцем вещи c id = " + itemId);
    }
}
