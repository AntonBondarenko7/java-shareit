package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.common.NotFoundException;

public class BookingItemOwnerException extends NotFoundException {
    public BookingItemOwnerException(Long userId, Long itemId) {
        super("Пользователь с id = " + userId + " владелец вещи с id = " + itemId);
    }

}
