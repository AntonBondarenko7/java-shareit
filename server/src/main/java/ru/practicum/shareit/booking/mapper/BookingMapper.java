package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingOwnerDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(uses = {ItemMapper.class, UserMapper.class})
public interface BookingMapper {

    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    BookingResponseDto toBookingResponseDto(Booking booking);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingOwnerDto toBookingOwnerDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "booker")
    Booking toBooking(BookingRequestDto bookingDto, User booker, Item item);

    List<BookingResponseDto> convertBookingListToBookingResponseDtoList(List<Booking> list);

}
