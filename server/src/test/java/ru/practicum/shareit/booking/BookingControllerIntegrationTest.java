package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.common.utils.Constants;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;
    private BookingResponseDto bookingResponseDto2;

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

        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setId(1L);
        bookingRequestDto.setStart(LocalDateTime.now().plusMinutes(5));
        bookingRequestDto.setEnd(LocalDateTime.now().plusHours(1));
        bookingRequestDto.setItemId(itemDto.getId());
        bookingRequestDto.setBooker(userDto);
        bookingRequestDto.setStatus(BookingStatus.WAITING);
        Booking booking = BookingMapper.INSTANCE.toBooking(bookingRequestDto, user, item);
        booking.setId(bookingRequestDto.getId());
        bookingResponseDto = BookingMapper.INSTANCE.toBookingResponseDto(booking);

        BookingRequestDto bookingRequestDto2 = new BookingRequestDto();
        bookingRequestDto2.setId(2L);
        bookingRequestDto2.setStart(LocalDateTime.now().plusMinutes(5));
        bookingRequestDto2.setEnd(LocalDateTime.now().plusHours(3));
        bookingRequestDto2.setItemId(itemDto2.getId());
        bookingRequestDto2.setBooker(userDto);
        bookingRequestDto2.setStatus(BookingStatus.WAITING);
        Booking booking2 = BookingMapper.INSTANCE.toBooking(bookingRequestDto2, user, item2);
        booking2.setId(bookingRequestDto2.getId());
        bookingResponseDto2 = BookingMapper.INSTANCE.toBookingResponseDto(booking2);
    }

    @SneakyThrows
    @Test
    void getAllBookingsByUser_whenInvoked_thenResponseWithBookingsCollectionInBody() {
        List<BookingResponseDto> bookings = Arrays.asList(bookingResponseDto, bookingResponseDto2);
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
        List<BookingResponseDto> bookings = Arrays.asList(bookingResponseDto, bookingResponseDto2);
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
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingResponseDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(Constants.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(bookingResponseDto), equalTo(result));
        verify(bookingService, times(1)).getBookingById(bookingId, userId);
    }

    @SneakyThrows
    @Test
    void createBooking_whenBookingValid_thenSavedBooking() {
        when(bookingService.createBooking(anyLong(), any(BookingRequestDto.class))).thenReturn(bookingResponseDto);

        String result = mockMvc.perform(post("/bookings")
                        .header(Constants.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(bookingResponseDto), equalTo(result));
        verify(bookingService, times(1))
                .createBooking(anyLong(), any(BookingRequestDto.class));
    }

//    @SneakyThrows
//    @Test
//    void createBooking_whenBookingNotValid__thenResponseStatusBadRequest() {
//        bookingRequestDto.setStart(LocalDateTime.now().minusMinutes(10));
//
//        String result = mockMvc.perform(post("/bookings")
//                        .header(Constants.HEADER_USER_ID, userId)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andReturn()
//                .getResponse()
//                .getContentAsString(StandardCharsets.UTF_8);
//
//        assertThat("{\"error\":\"Дата и время начала бронирования не могут быть в прошлом.\"}",
//                equalTo(result));
//        verify(bookingService, never()).createBooking(anyLong(), any(BookingRequestDto.class));
//    }

    @SneakyThrows
    @Test
    void updateBooking_whenBookingValid_thenUpdatedBooking() {
        long bookingId = 0L;
        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingResponseDto2);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(Constants.HEADER_USER_ID, userId)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingResponseDto2)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(bookingResponseDto2), equalTo(result));
        verify(bookingService, times(1))
                .updateBooking(userId, bookingId, true);
    }

    @SneakyThrows
    @Test
    void updateBooking_whenApprovedNotValid_thenUpdatedBooking() {
        long bookingId = 0L;

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(Constants.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingResponseDto2)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat("Произошла непредвиденная ошибка.", equalTo(result));
        verify(bookingService, never()).updateBooking(userId, bookingId, true);
    }

}