package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.ValidationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemOwnershipException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.QItem;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingService {


    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<BookingResponseDto> getAllBookingsByUser(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));

        List<Booking> bookings;
        switch (state) {
            case ("ALL"):
                bookings = bookingRepository.findByBookerIdOrderByEndDesc(userId);
                break;
            case ("CURRENT"):
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case ("PAST"):
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case ("FUTURE"):
                bookings = bookingRepository.findByBookerIdAndEndIsAfter(userId, LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case ("WAITING"):
                bookings = bookingRepository.findByBookerIdAndStatusOrderByEndDesc(userId, BookingStatus.WAITING);
                break;
            case ("REJECTED"):
                bookings = bookingRepository.findByBookerIdAndStatusOrderByEndDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new BookingStatusNotFoundException();
        }

        return BookingMapper.INSTANCE.convertBookingListToBookingResponseDtoList(bookings);
    }

    public List<BookingResponseDto> getAllBookingsAllItemsByOwner(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));

        List<Booking> bookings = new ArrayList<>();
        BooleanExpression byOwnerId = QItem.item.owner.id.eq(userId);
        switch (state) {
            case ("ALL"):
                bookingRepository.findAll(byOwnerId,
                                Sort.by(Sort.Direction.DESC, "start"))
                        .forEach(bookings::add);
                break;
            case ("CURRENT"):
                BooleanExpression byStart = QBooking.booking.start.before(LocalDateTime.now());
                BooleanExpression byEnd = QBooking.booking.end.after(LocalDateTime.now());
                bookingRepository.findAll(byOwnerId.and(byStart).and(byEnd),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .forEach(bookings::add);
                break;
            case ("PAST"):
                BooleanExpression byBeforeEnd = QBooking.booking.end.before(LocalDateTime.now());
                bookingRepository.findAll(byOwnerId.and(byBeforeEnd),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .forEach(bookings::add);
                break;
            case ("FUTURE"):
                BooleanExpression byAfterEnd = QBooking.booking.end.after(LocalDateTime.now());
                bookingRepository.findAll(byOwnerId.and(byAfterEnd),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .forEach(bookings::add);
                break;
            case ("WAITING"):
                BooleanExpression byStatusWaiting = QBooking.booking.status.eq(BookingStatus.WAITING);
                bookingRepository.findAll(byOwnerId.and(byStatusWaiting),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .forEach(bookings::add);
                break;
            case ("REJECTED"):
                BooleanExpression byStatusRejected = QBooking.booking.status.eq(BookingStatus.REJECTED);
                bookingRepository.findAll(byOwnerId.and(byStatusRejected),
                                Sort.by(Sort.Direction.DESC, "start"))
                        .forEach(bookings::add);
                break;
            default:
                throw new BookingStatusNotFoundException();
        }
        return BookingMapper.INSTANCE.convertBookingListToBookingResponseDtoList(bookings);
    }

    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException(bookingId));
        if ((!booking.getBooker().getId().equals(userId)) &&
                (!booking.getItem().getOwner().getId().equals(userId))) {
            throw new OtherBookerException(userId, bookingId);
        }

        return BookingMapper.INSTANCE.toBookingResponseDto(booking);
    }

    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Long userId) {
        BookingRequestDto bookingInDtoNew = validateBookingDto(bookingRequestDto);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));
        Item item = itemRepository.findById(bookingInDtoNew.getItemId()).orElseThrow(() ->
                new ItemNotFoundException(bookingInDtoNew.getItemId()));

        if (!item.getAvailable()) {
            throw new ValidationException("Ошибка! Вещь: " + ItemMapper.INSTANCE.toItemDto(item) +
                    " недоступна для бронирования.");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Пользователь с id = " + userId +
                    " владелец вещи с id = " + item.getId());
        }

        bookingInDtoNew.setStatus(BookingStatus.WAITING);
        Booking booking = BookingMapper.INSTANCE.toBooking(bookingInDtoNew, user, item);

        return BookingMapper.INSTANCE.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponseDto updateBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException(bookingId));
        Item item = booking.getItem();

        if (!item.getOwner().getId().equals(userId)) {
            throw new ItemOwnershipException(userId, item.getId());
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Статус бронирования с id = " + bookingId +
                    " не был изменён пользователем с id = " + userId);
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.INSTANCE.toBookingResponseDto(bookingRepository.saveAndFlush(booking));
    }

    private BookingRequestDto validateBookingDto(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart())) {
            throw new ValidationException("Ошибка! Дата и время начала бронирования должны быть раньше " +
                    "даты и времени конца бронирования.");
        }
        if (bookingRequestDto.getEnd().isEqual(bookingRequestDto.getStart())) {
            throw new ValidationException("Ошибка! Дата и время начала бронирования не могут совпадать с " +
                    "датой и временем конца бронирования.");
        }
        return bookingRequestDto;
    }

}