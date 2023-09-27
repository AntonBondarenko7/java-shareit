package ru.practicum.shareit.booking.dto;


import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import lombok.Data;

import static ru.practicum.shareit.common.utils.Constants.PATTERN_FOR_BOOKING;

@Data
public class BookingRequestDto {

    private Long id;

    @NotNull(message = "Дата и время начала бронирования не могут быть null.")
    @FutureOrPresent(message = "Дата и время начала бронирования не могут быть в прошлом.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_BOOKING)
    private LocalDateTime start;

    @NotNull(message = "Дата и время конца бронирования не могут быть null.")
    @FutureOrPresent(message = "Дата и время конца бронирования не могут быть в прошлом.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_BOOKING)
    private LocalDateTime end;

    @NotNull(message = "У бронирования должна быть вещь.")
    private Long itemId;

    private UserDto booker;

    private BookingStatus status;

}
