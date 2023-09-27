package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void getAllBookingsByUser_whenInvokedDefault_thenResponseStatusOkWithEmptyBody() {
        Long userId = 0L;
        List<BookingResponseDto> response = bookingController
                .getAllBookingsByUser(userId, BookingState.ALL, 0, 0);


        assertTrue(response.isEmpty());
        verify(bookingService, times(1))
                .getAllBookingsByUser(userId, BookingState.ALL, 0, 0);
    }

    @Test
    void getAllBookingsByUser_whenInvoked_thenResponseStatusOkWithBookingsCollectionInBody() {
        Long userId = 0L;
        List<BookingResponseDto> expectedBookings = Arrays.asList(new BookingResponseDto());
        when(bookingService.getAllBookingsByUser(userId, BookingState.ALL, 0, 0)).thenReturn(expectedBookings);

        List<BookingResponseDto> response = bookingController
                .getAllBookingsByUser(userId, BookingState.ALL, 0, 0);
        assertThat(expectedBookings, equalTo(response));
        verify(bookingService, times(1))
                .getAllBookingsByUser(userId, BookingState.ALL, 0, 0);
    }

    @Test
    void getAllBookingsAllItemsByOwner_whenInvokedDefault_thenResponseStatusOkWithEmptyBody() {
        Long userId = 0L;
        List<BookingResponseDto> response = bookingController
                .getAllBookingsAllItemsByOwner(userId, BookingState.ALL, 0, 0);
        assertTrue(response.isEmpty());
        verify(bookingService, times(1))
                .getAllBookingsAllItemsByOwner(userId, BookingState.ALL, 0, 0);
    }

    @Test
    void getAllBookingsAllItemsByOwner_whenInvoked_thenResponseStatusOkWithBookingsCollectionInBody() {
        Long userId = 0L;
        List<BookingResponseDto> expectedBookings = Arrays.asList(new BookingResponseDto());
        when(bookingService.getAllBookingsAllItemsByOwner(userId, BookingState.ALL, 0, 0))
                .thenReturn(expectedBookings);

        List<BookingResponseDto> response = bookingController
                .getAllBookingsAllItemsByOwner(userId, BookingState.ALL, 0, 0);


        assertThat(expectedBookings, equalTo(response));
        verify(bookingService, times(1))
                .getAllBookingsAllItemsByOwner(userId, BookingState.ALL, 0, 0);
    }

    @Test
    void getBookingById_whenBookingFound_thenReturnedBooking() {
        long bookingId = 0L;
        long userId = 0L;
        BookingResponseDto expectedBooking = new BookingResponseDto();
        when(bookingService.getBookingById(bookingId, userId)).thenReturn(expectedBooking);

        BookingResponseDto response = bookingController.getBookingById(bookingId, userId);


        assertThat(expectedBooking, equalTo(response));
        verify(bookingService, times(1)).getBookingById(bookingId, userId);
    }

    @Test
    void createBooking_whenBookingValid_thenSavedBooking() {
        BookingRequestDto bookingIn = new BookingRequestDto();
        BookingResponseDto expectedBooking = new BookingResponseDto();
        long userId = 0L;
        when(bookingService.createBooking(bookingIn, userId)).thenReturn(expectedBooking);

        BookingResponseDto response = bookingController
                .createBooking(bookingIn, userId);

        assertThat(expectedBooking, equalTo(response));
        verify(bookingService, times(1)).createBooking(bookingIn, userId);
    }

    @Test
    void updateBooking_whenBookingValid_thenUpdatedBooking() {
        Long bookingId = 0L;
        Long userId = 0L;
        BookingResponseDto newBooking = new BookingResponseDto();
        newBooking.setStatus(BookingStatus.APPROVED);
        when(bookingService.updateBooking(bookingId, null, userId)).thenReturn(newBooking);

        BookingResponseDto response = bookingController
                .updateBooking(bookingId, null, userId);

        assertThat(newBooking, equalTo(response));
        verify(bookingService, times(1))
                .updateBooking(bookingId, null, userId);
    }

}