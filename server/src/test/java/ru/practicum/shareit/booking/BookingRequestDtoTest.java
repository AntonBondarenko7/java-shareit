package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.common.utils.Constants.FORMATTER_FOR_BOOKING;

@JsonTest
class BookingRequestDtoTest {

    @Autowired
    private JacksonTester<BookingRequestDto> json;

    @Test
    void testBookingRequestDtoDto() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(5L);
        BookingRequestDto bookingDto = new BookingRequestDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        bookingDto.setItemId(2L);
        bookingDto.setBooker(userDto);
        bookingDto.setStatus(BookingStatus.WAITING);

        JsonContent<BookingRequestDto> result = json.write(bookingDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDto.getStart().format(FORMATTER_FOR_BOOKING));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDto.getEnd().format(FORMATTER_FOR_BOOKING));
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(bookingDto.getItemId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(bookingDto.getBooker().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingDto.getStatus().toString());
    }

}