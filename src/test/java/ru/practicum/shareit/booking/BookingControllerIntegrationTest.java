package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.utils.Constants;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    private final Long userId = 0L;
    private BookingRequestDto BookingRequestDto;
    private BookingResponseDto BookingResponseDto;
    private BookingResponseDto BookingResponseDto2;

    @BeforeEach
    public void addBookings() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("mail@mail.ru");
        User user = UserMapper.INSTANCE.toUser(userDto);
        user.setId(userDto.getId());

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("item 1");
        itemDto.setDescription("description 1");
        itemDto.setAvailable(true);
        Item item = ItemMapper.INSTANCE.toItem(itemDto, user);
        item.setId(itemDto.getId());
        ItemDto itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("item 2");
        itemDto2.setDescription("description 2");
        itemDto2.setAvailable(true);
        Item item2 = ItemMapper.INSTANCE.toItem(itemDto2, user);
        item2.setId(itemDto2.getId());

        BookingRequestDto = new BookingRequestDto();
        BookingRequestDto.setId(1L);
        BookingRequestDto.setStart(LocalDateTime.now().plusMinutes(5));
        BookingRequestDto.setEnd(LocalDateTime.now().plusHours(1));
        BookingRequestDto.setItemId(itemDto.getId());
        BookingRequestDto.setBooker(userDto);
        BookingRequestDto.setStatus(BookingStatus.WAITING);
        Booking booking = BookingMapper.INSTANCE.toBooking(BookingRequestDto, user, item);
        booking.setId(BookingRequestDto.getId());
        BookingResponseDto = BookingMapper.INSTANCE.toBookingResponseDto(booking);

        BookingRequestDto BookingRequestDto2 = new BookingRequestDto();
        BookingRequestDto2.setId(2L);
        BookingRequestDto2.setStart(LocalDateTime.now().plusMinutes(5));
        BookingRequestDto2.setEnd(LocalDateTime.now().plusHours(3));
        BookingRequestDto2.setItemId(itemDto2.getId());
        BookingRequestDto2.setBooker(userDto);
        BookingRequestDto2.setStatus(BookingStatus.WAITING);
        Booking booking2 = BookingMapper.INSTANCE.toBooking(BookingRequestDto2, user, item2);
        booking2.setId(BookingRequestDto2.getId());
        BookingResponseDto2 = BookingMapper.INSTANCE.toBookingResponseDto(booking2);
    }

    @SneakyThrows
    @Test
    void getAllBookingsByUser_whenInvoked_thenResponseStatusOkWithBookingsCollectionInBody() {
        List<BookingResponseDto> bookings = Arrays.asList(BookingResponseDto, BookingResponseDto2);
        when(bookingService.getAllBookingsByUser(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);

        String result = mockMvc.perform(get("/bookings")
                        .header(Constants.HEADER_USER_ID, userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(bookings), equalTo(result));
        verify(bookingService, times(1))
                .getAllBookingsByUser(userId, BookingState.ALL, 0, 5);
    }

    @SneakyThrows
    @Test
    void getAllBookingsByUser_whenStateNotValid_thenResponseStatusBadRequest() {
        String result = mockMvc.perform(get("/bookings")
                        .header(Constants.HEADER_USER_ID, userId)
                        .param("state", "not valid")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat("{\"error\":\"Unknown state: not valid\"}", equalTo(result));
        verify(bookingService, never()).getAllBookingsByUser(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getAllBookingsAllItemsByOwner_whenInvoked_thenResponseStatusOkWithBookingsCollectionInBody() {
        List<BookingResponseDto> bookings = Arrays.asList(BookingResponseDto, BookingResponseDto2);
        when(bookingService.getAllBookingsAllItemsByOwner(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);

        String result = mockMvc.perform(get("/bookings/owner")
                        .header(Constants.HEADER_USER_ID, userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(bookings), equalTo(result));
        verify(bookingService, times(1))
                .getAllBookingsAllItemsByOwner(userId, BookingState.ALL, 0, 5);
    }

    @SneakyThrows
    @Test
    void getBookingById_whenBookingFound_thenReturnedBooking() {
        long bookingId = 0L;
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(BookingResponseDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(Constants.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(BookingResponseDto), equalTo(result));
        verify(bookingService, times(1)).getBookingById(bookingId, userId);
    }

    @SneakyThrows
    @Test
    void createBooking_whenBookingValid_thenSavedBooking() {
        when(bookingService.createBooking(any(BookingRequestDto.class), anyLong())).thenReturn(BookingResponseDto);

        String result = mockMvc.perform(post("/bookings")
                        .header(Constants.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(BookingResponseDto), equalTo(result));
        verify(bookingService, times(1))
                .createBooking(any(BookingRequestDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void createBooking_whenBookingNotValid__thenResponseStatusBadRequest() {
        BookingRequestDto.setStart(LocalDateTime.now().minusMinutes(10));

        String result = mockMvc.perform(post("/bookings")
                        .header(Constants.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat("{\"error\":\"Дата и время начала бронирования не могут быть в прошлом.\"}",
                equalTo(result));
        verify(bookingService, never()).createBooking(any(BookingRequestDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void updateBooking_whenBookingValid_thenUpdatedBooking() {
        long bookingId = 0L;
        when(bookingService.updateBooking(anyLong(), anyBoolean(), anyLong())).thenReturn(BookingResponseDto2);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(Constants.HEADER_USER_ID, userId)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingResponseDto2)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(BookingResponseDto2), equalTo(result));
        verify(bookingService, times(1))
                .updateBooking(bookingId, true, userId);
    }

    @SneakyThrows
    @Test
    void updateBooking_whenApprovedNotValid_thenUpdatedBooking() {
        long bookingId = 0L;

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(Constants.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingResponseDto2)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat("Произошла непредвиденная ошибка.", equalTo(result));
        verify(bookingService, never()).updateBooking(bookingId, true, userId);
    }

}