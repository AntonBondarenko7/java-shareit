package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.common.exception.NotFoundException;

public class OtherBookerException extends NotFoundException {
    public OtherBookerException(Long userId, Long bookingId) {
        super("Пользователь с id = " + userId + " не осуществлял бронирование с id = : " + bookingId);
    }
}
