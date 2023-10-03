package ru.practicum.shareit.booking.controller;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.utils.Constants;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<BookingResponseDto> getAllBookingsByUser(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam Integer from, @RequestParam Integer size) {
        return bookingService.getAllBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingsAllItemsByOwner(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam Integer from, @RequestParam Integer size) {
        return bookingService.getAllBookingsAllItemsByOwner(
                userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@PathVariable Long bookingId,
                                             @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @PostMapping
    public BookingResponseDto createBooking(@RequestBody BookingRequestDto bookingRequestDto,
                                            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return bookingService.createBooking(userId, bookingRequestDto);
    }


    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBooking(@PathVariable Long bookingId, @RequestParam Boolean approved,
                                            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }
}