package ru.practicum.shareit.booking.service;


import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.BookingItemOwnerException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingNotSavedException;
import ru.practicum.shareit.booking.exception.OtherBookerException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemOwnershipException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.QItem;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.common.utils.ValidPage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    public List<BookingResponseDto> getAllBookingsByUser(Long userId, BookingState state, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));

        PageRequest page = ValidPage.validate(from, size);
        PageRequest pageRequest = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerIdOrderByEndDesc(userId, page);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(
                        userId, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndEndIsAfter(userId,
                        LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByEndDesc(
                        userId, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByEndDesc(
                        userId, BookingStatus.REJECTED, page);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return BookingMapper.INSTANCE.convertBookingListToBookingResponseDtoList(bookings);
    }

    public List<BookingResponseDto> getAllBookingsAllItemsByOwner(
            Long userId, BookingState state, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));

        ValidPage.validate(from, size);
        PageRequest page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookings = new ArrayList<>();
        BooleanExpression byOwnerId = QItem.item.owner.id.eq(userId);

        switch (state) {
            case ALL:
                bookings.addAll(bookingRepository.findAll(byOwnerId, page).getContent());
                break;

            case CURRENT:
                BooleanExpression byStart = QBooking.booking.start.before(LocalDateTime.now());
                BooleanExpression byEnd = QBooking.booking.end.after(LocalDateTime.now());
                bookings.addAll(bookingRepository
                        .findAll(byOwnerId.and(byStart).and(byEnd), page)
                        .getContent());
                break;

            case PAST:
                BooleanExpression byBeforeEnd = QBooking.booking.end.before(LocalDateTime.now());
                bookings.addAll(bookingRepository.findAll(byOwnerId.and(byBeforeEnd), page)
                        .getContent());
                break;

            case FUTURE:
                BooleanExpression byAfterEnd = QBooking.booking.end.after(LocalDateTime.now());
                bookings.addAll(bookingRepository.findAll(byOwnerId.and(byAfterEnd), page).getContent());
                break;

            case WAITING:
                BooleanExpression byStatusWaiting = QBooking.booking.status.eq(BookingStatus.WAITING);
                bookings.addAll(bookingRepository.findAll(byOwnerId.and(byStatusWaiting), page)
                        .getContent());
                break;

            case REJECTED:
                BooleanExpression byStatusRejected = QBooking.booking.status.eq(BookingStatus.REJECTED);
                bookings.addAll(bookingRepository.findAll(byOwnerId.and(byStatusRejected), page)
                        .getContent());
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
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
    public BookingResponseDto createBooking(Long userId, BookingRequestDto bookingRequestDto) {
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
            throw new BookingItemOwnerException(userId, item.getId());
        }

        bookingInDtoNew.setStatus(BookingStatus.WAITING);
        Booking booking = BookingMapper.INSTANCE.toBooking(bookingInDtoNew, user, item);

        try {
            return BookingMapper.INSTANCE.toBookingResponseDto(bookingRepository.save(booking));
        } catch (DataIntegrityViolationException e) {
            throw new BookingNotSavedException();
        }

    }

    @Transactional
    public BookingResponseDto updateBooking(Long userId, Long bookingId, Boolean approved) {
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

        try {
            return BookingMapper.INSTANCE.toBookingResponseDto(bookingRepository.saveAndFlush(booking));
        } catch (DataIntegrityViolationException e) {
            throw new BookingNotSavedException();
        }
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