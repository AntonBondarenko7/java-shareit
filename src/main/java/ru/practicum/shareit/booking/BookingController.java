package ru.practicum.shareit.booking;

import javax.validation.Valid;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Constants;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<BookingResponseDto> getAllBookingsByUser(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        List<BookingResponseDto> bookingResponseDtos = bookingService.getAllBookingsByUser(userId, state);
        log.info("Получен список всех бронирований текущего пользователя с id = {}, количество = {}.",
                userId, bookingResponseDtos.size());
        return bookingResponseDtos;
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingsAllItemsByOwner(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        List<BookingResponseDto> bookingResponseDtos = bookingService.getAllBookingsAllItemsByOwner(userId, state);
        log.info("Получен список всех бронирований для всех вещей текущего пользователя с id = {}, " +
                "количество = {}.", userId, bookingResponseDtos.size());
        return bookingResponseDtos;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@PathVariable Long bookingId,
                                                        @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        BookingResponseDto bookingResponseDto = bookingService.getBookingById(userId, bookingId);
        log.info("Получено бронирование с id = {}.", bookingId);
        return bookingResponseDto;
    }

    @PostMapping
    @Validated
    public BookingResponseDto saveBooking(@Valid @RequestBody BookingRequestDto bookingRequestDto,
                                                     @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        BookingResponseDto bookingResponseDto = bookingService.createBooking(bookingRequestDto, userId);
        log.info("Добавлен новый запрос на бронирование: {}", bookingResponseDto);
        return bookingResponseDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateItem(@PathVariable Long bookingId, @RequestParam Boolean approved,
                                                    @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        BookingResponseDto bookingResponseDto = bookingService.updateBooking(bookingId, approved, userId);
        log.info("Обновлено бронирование: {}.", bookingResponseDto);
        return bookingResponseDto;
    }
}