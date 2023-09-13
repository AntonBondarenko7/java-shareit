package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.common.NotFoundException;

public class BookingNotFoundException extends NotFoundException {

    public BookingNotFoundException(Long bookingId) {
        super("Бронирование с идентификатором " + bookingId + " не найдена.");
    }

}
