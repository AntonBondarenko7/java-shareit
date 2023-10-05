package ru.practicum.shareit.request;

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
import ru.practicum.shareit.common.utils.Constants;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;

    private final Long userId = 0L;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDto itemRequestDto2;

    @BeforeEach
    public void addBookings() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("mail@mail.ru");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("item 1");
        itemDto.setDescription("description 1");
        itemDto.setAvailable(true);
        ItemDto itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("item 2");
        itemDto2.setDescription("description 2");
        itemDto2.setAvailable(true);

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("description 1");
        itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setId(2L);
        itemRequestDto2.setDescription("description 2");
    }

    @SneakyThrows
    @Test
    void getAllItemRequestsByUser_whenInvoked_thenResponseStatusOkWithItemRequestsCollectionInBody() {
        List<ItemRequestDto> itemRequests = Arrays.asList(itemRequestDto, itemRequestDto2);
        when(itemRequestService.getAllItemRequestsByUser(anyLong()))
                .thenReturn(itemRequests);

        String result = mockMvc.perform(get("/requests")
                        .header(Constants.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(itemRequests), equalTo(result));
        verify(itemRequestService, times(1)).getAllItemRequestsByUser(userId);
    }

    @SneakyThrows
    @Test
    void getAllItemRequestsByOtherUsers_whenInvoked_thenResponseStatusOkWithItemRequestsCollectionInBody() {
        List<ItemRequestDto> itemRequests = List.of(itemRequestDto, itemRequestDto2);
        when(itemRequestService.getAllItemRequestsByOtherUsers(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemRequests);

        String result = mockMvc.perform(get("/requests/all")
                        .header(Constants.HEADER_USER_ID, userId)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(itemRequests), equalTo(result));
        verify(itemRequestService, times(1))
                .getAllItemRequestsByOtherUsers(userId, 0, 10);
    }

    @SneakyThrows
    @Test
    void getItemRequestById_whenItemRequestFound_thenReturnedItemRequest() {
        long itemRequestId = 0L;
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(itemRequestDto);

        String result = mockMvc.perform(get("/requests/{itemRequestId}", itemRequestId)
                        .header(Constants.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(itemRequestDto), equalTo(result));
        verify(itemRequestService, times(1)).getItemRequestById(userId, itemRequestId);
    }

    @SneakyThrows
    @Test
    void saveItemRequest_whenItemRequestValid_thenSavedItemRequest() {
        when(itemRequestService.createItemRequest(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(itemRequestDto);

        String result = mockMvc.perform(post("/requests")
                        .header(Constants.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(itemRequestDto), equalTo(result));
        verify(itemRequestService, times(1))
                .createItemRequest(anyLong(), any(ItemRequestDto.class));
    }

}