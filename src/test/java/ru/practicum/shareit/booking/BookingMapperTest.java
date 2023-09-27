package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingOwnerDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

class BookingMapperTest {

    private final BookingMapperImpl bookingMapper = new BookingMapperImpl();

    @Test
    void toBookingResponseDto() {
        BookingResponseDto BookingResponseDTO = bookingMapper.toBookingResponseDto(null);

        assertThat(BookingResponseDTO, nullValue());
    }

    @Test
    void toBookingOwnerDtoWithEmpty() {
        Booking booking = new Booking();
        booking.setId(1L);

        BookingOwnerDto BookingOwnerDTO = bookingMapper.toBookingOwnerDto(booking);

        assertThat(BookingOwnerDTO.getId(), equalTo(booking.getId()));
    }

    @Test
    void toBookingOwnerDtoWithEmptyId() {
        User user = new User();
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);

        BookingOwnerDto BookingOwnerDTO = bookingMapper.toBookingOwnerDto(booking);

        assertThat(BookingOwnerDTO.getId(), equalTo(booking.getId()));
    }

    @Test
    void toBookingOwnerDto() {
        User user = new User();
        user.setId(9L);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);

        BookingOwnerDto BookingOwnerDTO = bookingMapper.toBookingOwnerDto(booking);

        assertThat(BookingOwnerDTO.getId(), equalTo(booking.getId()));
        assertThat(BookingOwnerDTO.getBookerId(), equalTo(booking.getBooker().getId()));
    }

    @Test
    void toBooking() {
        Booking booking = bookingMapper.toBooking(null, null, null);

        assertThat(booking, nullValue());
    }

    @Test
    void convertBookingListToBookingResponseDtoList() {
        List<BookingResponseDto> bookings = bookingMapper.convertBookingListToBookingResponseDtoList(null);

        assertThat(bookings, nullValue());
    }

}