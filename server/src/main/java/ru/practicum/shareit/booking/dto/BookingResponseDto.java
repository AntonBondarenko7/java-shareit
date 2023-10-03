package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.shareit.common.utils.Constants.PATTERN_FOR_BOOKING;

@Data
public class BookingResponseDto {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_BOOKING)
    private LocalDateTime start;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_BOOKING)
    private LocalDateTime end;

    private ItemShortDto item;

    private UserShortDto booker;

    private BookingStatus status;

}
