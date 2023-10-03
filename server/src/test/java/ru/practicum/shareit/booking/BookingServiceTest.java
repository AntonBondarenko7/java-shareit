package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingNotSavedException;
import ru.practicum.shareit.booking.exception.BookingItemOwnerException;
import ru.practicum.shareit.booking.exception.OtherBookerException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemOwnershipException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.querydsl.core.types.dsl.BooleanExpression;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void getAllBookingsByUser_whenInvoked_thenReturnedEmptyList() {
        Long userId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        List<BookingResponseDto> actualItems = bookingService
                .getAllBookingsByUser(userId, BookingState.ALL, 0, 1);

        assertThat(actualItems, empty());
        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1))
                .findByBookerIdOrderByEndDesc(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllBookingsByUser_whenInvokedCurrent_thenReturnedBookingsCollectionInList() {
        Long userId = 0L;
        List<Booking> expectedBookings = Arrays.asList(new Booking(), new Booking());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(expectedBookings);

        List<BookingResponseDto> actualBookings = bookingService
                .getAllBookingsByUser(userId, BookingState.CURRENT, 0, 1);

        assertThat(BookingMapper.INSTANCE.convertBookingListToBookingResponseDtoList(expectedBookings),
                equalTo(actualBookings));
        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1))
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(anyLong(),
                        any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsByUser_whenInvokedPast_thenReturnedBookingsCollectionInList() {
        Long userId = 0L;
        List<Booking> expectedBookings = Arrays.asList(new Booking(), new Booking());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findByBookerIdAndEndIsBefore(
                anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(expectedBookings);

        List<BookingResponseDto> actualBookings = bookingService
                .getAllBookingsByUser(userId, BookingState.PAST, 0, 1);

        assertThat(BookingMapper.INSTANCE.convertBookingListToBookingResponseDtoList(expectedBookings),
                equalTo(actualBookings));
        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1))
                .findByBookerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsByUser_whenInvokedFuture_thenReturnedBookingsCollectionInList() {
        Long userId = 0L;
        List<Booking> expectedBookings = Arrays.asList(new Booking(), new Booking());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findByBookerIdAndEndIsAfter(
                anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(expectedBookings);

        List<BookingResponseDto> actualBookings = bookingService
                .getAllBookingsByUser(userId, BookingState.FUTURE, 0, 1);

        assertThat(BookingMapper.INSTANCE.convertBookingListToBookingResponseDtoList(expectedBookings),
                equalTo(actualBookings));
        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1))
                .findByBookerIdAndEndIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsByUser_whenInvokedWaiting_thenReturnedBookingsCollectionInList() {
        Long userId = 0L;
        List<Booking> expectedBookings = Arrays.asList(new Booking(), new Booking());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findByBookerIdAndStatusOrderByEndDesc(
                anyLong(), any(), any(Pageable.class))).thenReturn(expectedBookings);

        List<BookingResponseDto> actualBookings = bookingService
                .getAllBookingsByUser(userId, BookingState.WAITING, 0, 1);

        assertThat(BookingMapper.INSTANCE.convertBookingListToBookingResponseDtoList(expectedBookings),
                equalTo(actualBookings));
        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1))
                .findByBookerIdAndStatusOrderByEndDesc(anyLong(), any(), any(Pageable.class));
    }

    @Test
    void getAllBookingsByUser_whenInvokedRejected_thenReturnedBookingsCollectionInList() {
        Long userId = 0L;
        List<Booking> expectedBookings = Arrays.asList(new Booking(), new Booking());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findByBookerIdAndStatusOrderByEndDesc(
                anyLong(), any(), any(Pageable.class))).thenReturn(expectedBookings);

        List<BookingResponseDto> actualBookings = bookingService
                .getAllBookingsByUser(userId, BookingState.REJECTED, 0, 1);

        assertThat(BookingMapper.INSTANCE.convertBookingListToBookingResponseDtoList(expectedBookings),
                equalTo(actualBookings));
        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1))
                .findByBookerIdAndStatusOrderByEndDesc(anyLong(), any(), any(Pageable.class));
    }

    @Test
    void getAllBookingsByUser_whenUserNotFound_thenExceptionThrown() {
        Long userId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.getAllBookingsByUser(userId, BookingState.REJECTED, 0, 1));

        assertThat("Пользователь с идентификатором 0 не найден.", equalTo(exception.getMessage()));

        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, never())
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(anyLong(),
                        any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsAllItemsByOwner_whenInvokedAll_thenReturnedBookingsCollectionInList() {
        Long userId = 0L;
        List<Booking> expectedBookingsList = Arrays.asList(new Booking(), new Booking());
        Page<Booking> expectedBookingsPage = new PageImpl<>(
                expectedBookingsList, PageRequest.of(0, 1), 2);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(expectedBookingsPage);

        List<BookingResponseDto> actualBookings = bookingService
                .getAllBookingsAllItemsByOwner(userId, BookingState.ALL, 0, 1);

        assertThat(BookingMapper.INSTANCE.convertBookingListToBookingResponseDtoList(expectedBookingsList),
                equalTo(actualBookings));
        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsAllItemsByOwner_whenInvokedCurrent_thenReturnedBookingsCollectionInList() {
        Long userId = 0L;
        List<Booking> expectedBookingsList = Arrays.asList(new Booking(), new Booking());
        Page<Booking> expectedBookingsPage = new PageImpl<>(
                expectedBookingsList, PageRequest.of(0, 1), 2);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(expectedBookingsPage);

        List<BookingResponseDto> actualBookings = bookingService
                .getAllBookingsAllItemsByOwner(userId, BookingState.CURRENT, 0, 1);

        assertThat(BookingMapper.INSTANCE.convertBookingListToBookingResponseDtoList(expectedBookingsList),
                equalTo(actualBookings));
        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test

    void getAllBookingsAllItemsByOwner_whenInvokedPast_thenReturnedBookingsCollectionInList() {
        Long userId = 0L;
        List<Booking> expectedBookingsList = Arrays.asList(new Booking(), new Booking());
        Page<Booking> expectedBookingsPage = new PageImpl<>(
                expectedBookingsList, PageRequest.of(0, 1), 2);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(expectedBookingsPage);

        List<BookingResponseDto> actualBookings = bookingService
                .getAllBookingsAllItemsByOwner(userId, BookingState.PAST, 0, 1);

        assertThat(BookingMapper.INSTANCE.convertBookingListToBookingResponseDtoList(expectedBookingsList),
                equalTo(actualBookings));
        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsAllItemsByOwner_whenInvokedFuture_thenReturnedBookingsCollectionInList() {
        Long userId = 0L;
        List<Booking> expectedBookingsList = Arrays.asList(new Booking(), new Booking());
        Page<Booking> expectedBookingsPage = new PageImpl<>(
                expectedBookingsList, PageRequest.of(0, 1), 2);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(expectedBookingsPage);

        List<BookingResponseDto> actualBookings = bookingService
                .getAllBookingsAllItemsByOwner(userId, BookingState.FUTURE, 0, 1);

        assertThat(BookingMapper.INSTANCE.convertBookingListToBookingResponseDtoList(expectedBookingsList),
                equalTo(actualBookings));
        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsAllItemsByOwner_whenInvokedWaiting_thenReturnedBookingsCollectionInList() {
        Long userId = 0L;
        List<Booking> expectedBookingsList = Arrays.asList(new Booking(), new Booking());
        Page<Booking> expectedBookingsPage = new PageImpl<>(
                expectedBookingsList, PageRequest.of(0, 1), 2);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(expectedBookingsPage);

        List<BookingResponseDto> actualBookings = bookingService
                .getAllBookingsAllItemsByOwner(userId, BookingState.WAITING, 0, 1);

        assertThat(BookingMapper.INSTANCE.convertBookingListToBookingResponseDtoList(expectedBookingsList),
                equalTo(actualBookings));
        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsAllItemsByOwner_whenInvokedRejected_thenReturnedBookingsCollectionInList() {
        Long userId = 0L;
        List<Booking> expectedBookingsList = Arrays.asList(new Booking(), new Booking());
        Page<Booking> expectedBookingsPage = new PageImpl<>(
                expectedBookingsList, PageRequest.of(0, 1), 2);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAll(any(BooleanExpression.class), any(Pageable.class)))
                .thenReturn(expectedBookingsPage);

        List<BookingResponseDto> actualBookings = bookingService
                .getAllBookingsAllItemsByOwner(userId, BookingState.REJECTED, 0, 1);

        assertThat(BookingMapper.INSTANCE.convertBookingListToBookingResponseDtoList(expectedBookingsList),
                equalTo(actualBookings));
        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1))
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsAllItemsByOwner_whenUserNotFound_thenExceptionThrown() {
        Long userId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.getAllBookingsAllItemsByOwner(userId, BookingState.REJECTED, 0, 1));

        assertThat("Пользователь с идентификатором 0 не найден.", equalTo(exception.getMessage()));

        InOrder inOrder = inOrder(userRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, never())
                .findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    void getBookingById_whenBookingFound_thenReturnedItem() {
        long bookingId = 0L;
        long userId = 0L;
        Booking expectedBooking = new Booking();
        User user = new User();
        user.setId(userId);
        expectedBooking.setBooker(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));

        BookingResponseDto actualBooking = bookingService.getBookingById(bookingId, userId);

        assertThat(BookingMapper.INSTANCE.toBookingResponseDto(expectedBooking), equalTo(actualBooking));
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBookingById_whenBookingNotFound_thenExceptionThrown() {
        long bookingId = 0L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        final BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(bookingId, 0L));

        assertThat("Бронирование с идентификатором 0 не найдено.", equalTo(exception.getMessage()));
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBookingById_whenBookingNotValid_thenExceptionThrown() {
        long userId = 0L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setOwner(user);

        long bookingId = 1L;
        Booking expectedBooking = new Booking();
        expectedBooking.setItem(item);
        expectedBooking.setBooker(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));

        final OtherBookerException exception = assertThrows(OtherBookerException.class,
                () -> bookingService.getBookingById(bookingId, 1L));

        assertThat("Пользователь с id = 1 не осуществлял бронирование с id = 1",
                equalTo(exception.getMessage()));
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void createBooking_whenBookingValid_thenSavedBooking() {
        Long userId = 0L;
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        Long itemId = 0L;
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(user);

        BookingRequestDto bookingToSave = new BookingRequestDto();
        bookingToSave.setItemId(itemId);
        bookingToSave.setStart(LocalDateTime.now());
        bookingToSave.setEnd(LocalDateTime.now().plusMinutes(1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(BookingMapper.INSTANCE.toBooking(bookingToSave, user, item));

        BookingResponseDto actualBooking = bookingService.createBooking(userId, bookingToSave);

        assertThat(bookingToSave.getId(), equalTo(actualBooking.getId()));
        assertThat(bookingToSave.getStart(), equalTo(actualBooking.getStart()));
        assertThat(bookingToSave.getEnd(), equalTo(actualBooking.getEnd()));
        assertThat(bookingToSave.getItemId(), equalTo(actualBooking.getItem().getId()));
        assertThat(1L, equalTo(actualBooking.getBooker().getId()));
        assertThat(null, equalTo(actualBooking.getStatus()));

        InOrder inOrder = inOrder(userRepository, itemRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(userId);
        inOrder.verify(itemRepository, times(1)).findById(itemId);
        inOrder.verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_whenDateTimeNotValid1_thenExceptionThrown() {
        Long userId = 0L;
        Long itemId = 0L;
        BookingRequestDto bookingToSave = new BookingRequestDto();
        bookingToSave.setItemId(itemId);
        bookingToSave.setStart(LocalDateTime.now());
        bookingToSave.setEnd(LocalDateTime.now().minusMinutes(1));

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(userId, bookingToSave));

        assertThat("Ошибка! Дата и время начала бронирования должны быть раньше даты и времени " +
                "конца бронирования.", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRepository, bookingRepository);
        inOrder.verify(userRepository, never()).findById(anyLong());
        inOrder.verify(itemRepository, never()).findById(anyLong());
        inOrder.verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_whenDateTimeNotValid2_thenExceptionThrown() {
        Long userId = 0L;
        Long itemId = 0L;
        LocalDateTime dateTimeBooking = LocalDateTime.now();
        BookingRequestDto bookingToSave = new BookingRequestDto();
        bookingToSave.setItemId(itemId);
        bookingToSave.setStart(dateTimeBooking);
        bookingToSave.setEnd(dateTimeBooking);

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(userId, bookingToSave));

        assertThat("Ошибка! Дата и время начала бронирования не могут совпадать с датой и временем " +
                "конца бронирования.", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRepository, bookingRepository);
        inOrder.verify(userRepository, never()).findById(anyLong());
        inOrder.verify(itemRepository, never()).findById(anyLong());
        inOrder.verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_whenUserNotFound_thenExceptionThrown() {
        Long userId = 0L;
        Long itemId = 0L;
        BookingRequestDto bookingToSave = new BookingRequestDto();
        bookingToSave.setItemId(itemId);
        bookingToSave.setStart(LocalDateTime.now());
        bookingToSave.setEnd(LocalDateTime.now().plusMinutes(1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.createBooking(userId, bookingToSave));

        assertThat("Пользователь с идентификатором 0 не найден.", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRepository, never()).findById(anyLong());
        inOrder.verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_whenItemNotFound_thenExceptionThrown() {
        Long userId = 0L;
        User user = new User();
        Long itemId = 0L;

        BookingRequestDto bookingToSave = new BookingRequestDto();
        bookingToSave.setItemId(itemId);
        bookingToSave.setStart(LocalDateTime.now());
        bookingToSave.setEnd(LocalDateTime.now().plusMinutes(1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        final ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> bookingService.createBooking(userId, bookingToSave));

        assertThat("Вещь с идентификатором 0 не найдена.", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, never()).save(any(Booking.class));
    }


    @Test
    void createBooking_whenItemNotValid_thenExceptionThrown() {
        Long userId = 0L;
        User user = new User();
        Item item = new Item();
        Long itemId = 0L;
        item.setAvailable(false);

        BookingRequestDto bookingToSave = new BookingRequestDto();
        bookingToSave.setItemId(itemId);
        bookingToSave.setStart(LocalDateTime.now());
        bookingToSave.setEnd(LocalDateTime.now().plusMinutes(1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(userId, bookingToSave));

        assertThat("Ошибка! Вещь: " + ItemMapper.INSTANCE.toItemDto(item) +
                " недоступна для бронирования.", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_whenOwnerNotValid_thenExceptionThrown() {
        Long userId = 0L;
        User user = new User();
        user.setId(userId);
        Long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(user);

        BookingRequestDto bookingToSave = new BookingRequestDto();
        bookingToSave.setItemId(itemId);
        bookingToSave.setStart(LocalDateTime.now());
        bookingToSave.setEnd(LocalDateTime.now().plusMinutes(1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        final BookingItemOwnerException exception = assertThrows(BookingItemOwnerException.class,
                () -> bookingService.createBooking(userId, bookingToSave));

        assertThat("Пользователь с id = 0 владелец вещи с id = 0", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_whenBookingNotSaved_thenExceptionThrown() {
        Long userId = 0L;
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        Long itemId = 0L;
        item.setAvailable(true);
        item.setOwner(user);

        BookingRequestDto bookingToSave = new BookingRequestDto();
        bookingToSave.setItemId(itemId);
        bookingToSave.setStart(LocalDateTime.now());
        bookingToSave.setEnd(LocalDateTime.now().plusMinutes(1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenThrow(new BookingNotSavedException());

        final BookingNotSavedException exception = assertThrows(BookingNotSavedException.class,
                () -> bookingService.createBooking(userId, bookingToSave));

        assertThat("Не удалось сохранить бронирование", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRepository, bookingRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRepository, times(1)).findById(anyLong());
        inOrder.verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void updateBooking_whenBookingApproved_thenUpdatedBooking() {
        Long userId = 0L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setOwner(user);

        Long bookingId = 0L;
        Booking oldBooking = new Booking();
        oldBooking.setItem(item);
        oldBooking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(oldBooking));

        Booking newBooking = new Booking();
        newBooking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(newBooking);

        BookingResponseDto actualBooking = bookingService.updateBooking(userId, bookingId, true);

        assertThat(newBooking.getId(), equalTo(actualBooking.getId()));
        assertThat(newBooking.getStart(), equalTo(actualBooking.getStart()));
        assertThat(newBooking.getEnd(), equalTo(actualBooking.getEnd()));
        assertThat(newBooking.getItem(), equalTo(actualBooking.getItem()));
        assertThat(newBooking.getBooker(), equalTo(actualBooking.getBooker()));
        assertThat(newBooking.getStatus(), equalTo(actualBooking.getStatus()));

        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).saveAndFlush(any(Booking.class));
    }

    @Test
    void updateBooking_whenBookingRejected_thenUpdatedBooking() {
        Long userId = 0L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setOwner(user);

        Long bookingId = 0L;
        Booking oldBooking = new Booking();
        oldBooking.setItem(item);
        oldBooking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(oldBooking));

        Booking newBooking = new Booking();
        newBooking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(newBooking);

        BookingResponseDto actualBooking = bookingService.updateBooking(userId, bookingId, false);

        assertThat(newBooking.getId(), equalTo(actualBooking.getId()));
        assertThat(newBooking.getStart(), equalTo(actualBooking.getStart()));
        assertThat(newBooking.getEnd(), equalTo(actualBooking.getEnd()));
        assertThat(newBooking.getItem(), equalTo(actualBooking.getItem()));
        assertThat(newBooking.getBooker(), equalTo(actualBooking.getBooker()));
        assertThat(newBooking.getStatus(), equalTo(actualBooking.getStatus()));

        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).saveAndFlush(any(Booking.class));
    }

    @Test
    void updateBooking_whenBookingNotFound_thenExceptionThrown() {
        Long userId = 0L;
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(user);
        Long bookingId = 0L;
        Booking oldBooking = new Booking();
        oldBooking.setItem(item);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        final BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.updateBooking(userId, bookingId, true));

        assertThat("Бронирование с идентификатором 0 не найдено.", equalTo(exception.getMessage()));
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).saveAndFlush(any(Booking.class));
    }

    @Test
    void updateBooking_whenBookingNotValid_thenExceptionThrown() {
        Long userId = 0L;
        User user = new User();
        user.setId(1L);
        Long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(user);
        Long bookingId = 0L;
        Booking oldBooking = new Booking();
        oldBooking.setItem(item);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(oldBooking));

        final ItemOwnershipException exception = assertThrows(ItemOwnershipException.class,
                () -> bookingService.updateBooking(userId, bookingId, true));

        assertThat("Пользователь с id = 0 не является владельцем вещи c id = 0",
                equalTo(exception.getMessage()));
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).saveAndFlush(any(Booking.class));
    }

    @Test
    void updateBooking_whenStatusNotValid_thenExceptionThrown() {
        Long userId = 0L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setOwner(user);

        Long bookingId = 0L;
        Booking oldBooking = new Booking();
        oldBooking.setItem(item);
        oldBooking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(oldBooking));

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.updateBooking(userId, bookingId, false));

        assertThat("Статус бронирования с id = 0 не был изменён пользователем с id = 0",
                equalTo(exception.getMessage()));
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).saveAndFlush(any(Booking.class));
    }

    @Test
    void updateBooking_whenBookingNotUpdate_thenExceptionThrown() {
        Long userId = 0L;
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setOwner(user);

        Long bookingId = 0L;
        Booking oldBooking = new Booking();
        oldBooking.setItem(item);
        oldBooking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(oldBooking));
        when(bookingRepository.saveAndFlush(any(Booking.class)))
                .thenThrow(new BookingNotSavedException());

        final BookingNotSavedException exception = assertThrows(BookingNotSavedException.class,
                () -> bookingService.updateBooking(userId, bookingId, true));

        assertThat("Не удалось сохранить бронирование", equalTo(exception.getMessage()));
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).saveAndFlush(any(Booking.class));
    }

}