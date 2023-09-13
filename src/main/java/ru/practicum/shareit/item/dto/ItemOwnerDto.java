package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.booking.dto.BookingOwnerDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

@Data
@EqualsAndHashCode
public class ItemOwnerDto {

    private BookingOwnerDto lastBooking;
    private BookingOwnerDto nextBooking;
    private List<CommentDto> comments;

}
