package ru.practicum.shareit.booking.exception;

import ru.practicum.shareit.common.exception.NotSavedException;

public class BookingNotSavedException extends NotSavedException {

    public BookingNotSavedException() {
        super("Не удалось сохранить бронирование");
    }

}
