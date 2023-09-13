package ru.practicum.shareit.booking.exception;

public class OtherBookerException extends RuntimeException {
    public OtherBookerException(Long userId, Long bookingId) {
        super("Пользователь с id = " + userId + " не осуществлял бронирование с id = : " + bookingId);
    }
}
