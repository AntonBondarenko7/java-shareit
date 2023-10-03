package ru.practicum.shareit.request;

import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Test
    void getAllItemRequestsByUser_whenInvokedDefault_thenResponseStatusOkWithEmptyBody() {
        Long userId = 0L;
        List<ItemRequestDto> response = itemRequestController
                .getAllItemRequestsByUser(userId);

        assertTrue(response.isEmpty());
        verify(itemRequestService, times(1)).getAllItemRequestsByUser(userId);
    }

    @Test
    void getAllItemRequestsByUser_whenInvoked_thenResponseStatusOkWithItemRequestsCollectionInBody() {
        Long userId = 0L;
        List<ItemRequestDto> expectedItemRequests = Arrays.asList(new ItemRequestDto());
        when(itemRequestService.getAllItemRequestsByUser(userId)).thenReturn(expectedItemRequests);

        List<ItemRequestDto> response = itemRequestController
                .getAllItemRequestsByUser(userId);

        assertThat(expectedItemRequests, equalTo(response));
        verify(itemRequestService, times(1)).getAllItemRequestsByUser(userId);
    }

    @Test

    void getAllItemRequestsByOtherUsers_whenInvokedDefault_thenResponseStatusOkWithEmptyBody() {
        Long userId = 0L;
        List<ItemRequestDto> response = itemRequestController
                .getAllItemRequestsByOtherUsers(userId, 0, 0);

        assertTrue(response.isEmpty());
        verify(itemRequestService, times(1))
                .getAllItemRequestsByOtherUsers(userId, 0, 0);

    }

    @Test
    void getAllItemRequestsByOtherUsers_whenInvoked_thenResponseStatusOkWithItemRequestsCollectionInBody() {
        Long userId = 0L;
        List<ItemRequestDto> expectedItemRequests = Arrays.asList(new ItemRequestDto());
        when(itemRequestService.getAllItemRequestsByOtherUsers(userId, 0, 0))
                .thenReturn(expectedItemRequests);

        List<ItemRequestDto> response = itemRequestController
                .getAllItemRequestsByOtherUsers(userId, 0, 0);

        assertThat(expectedItemRequests, equalTo(response));
        verify(itemRequestService, times(1))
                .getAllItemRequestsByOtherUsers(userId, 0, 0);
    }

    @Test
    void getItemRequestById_whenItemRequestFound_thenReturnedItemRequest() {
        long requestId = 0L;
        long userId = 0L;
        ItemRequestDto expectedItemRequest = new ItemRequestDto();
        when(itemRequestService.getItemRequestById(requestId, userId)).thenReturn(expectedItemRequest);

        ItemRequestDto response = itemRequestController
                .getItemRequestById(requestId, userId);

        assertThat(expectedItemRequest, equalTo(response));
        verify(itemRequestService, times(1))
                .getItemRequestById(requestId, userId);
    }

    @Test
    void saveItemRequest_whenItemRequestValid_thenSavedItemRequest() {
        ItemRequestDto expectedItemRequest = new ItemRequestDto();
        long userId = 0L;
        when(itemRequestService.createItemRequest(expectedItemRequest, userId))
                .thenReturn(expectedItemRequest);

        ItemRequestDto response = itemRequestController
                .saveItemRequest(expectedItemRequest, userId);

        assertThat(expectedItemRequest, equalTo(response));
        verify(itemRequestService, times(1))
                .createItemRequest(expectedItemRequest, userId);
    }

}