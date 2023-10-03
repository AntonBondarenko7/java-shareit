package ru.practicum.shareit.booking.controller;

import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.utils.Constants;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByUser(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingState state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен список всех бронирований текущего пользователя с id = {}, state = {}, " +
                "from = {}, size = {}.", userId, state, from, size);
        return bookingClient.getAllBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsAllItemsByOwner(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingState state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен список всех бронирований для всех вещей владельца с id = {}, state = {}, " +
                "from = {}, size = {}.", userId, state, from, size);
        return bookingClient.getAllBookingsAllItemsByOwner(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @PathVariable Long bookingId) {
        log.info("Получено бронирование с id = {}, userId={}.", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> saveBooking(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @Valid @RequestBody BookingRequestDto bookingInDto) {
        log.info("Добавлен новый запрос на бронирование: {}, userId={}.", bookingInDto, userId);
        return bookingClient.saveBooking(userId, bookingInDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @PathVariable Long bookingId, @RequestParam Boolean approved) {
        log.info("Обновлено бронирование с id = {}, userId={}, approved = {}.", bookingId, userId, approved);
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

}