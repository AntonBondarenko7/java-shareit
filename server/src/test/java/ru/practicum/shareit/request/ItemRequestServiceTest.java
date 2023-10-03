package ru.practicum.shareit.request;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.exception.ItemRequestNotSavedException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestService itemRequestService;

    @Test
    void getAllItemRequestsByUser_whenInvoked_thenReturnedEmptyList() {
        Long userId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        List<ItemRequestDto> actualItemRequests = itemRequestService.getAllItemRequestsByUser(userId);

        assertThat(actualItemRequests, empty());
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, times(1))
                .findAllByRequesterId(anyLong());
    }

    @Test
    void getAllItemRequestsByUser_whenInvoked_thenReturneItemRequestsCollectionInList() {
        Long userId = 0L;
        List<ItemRequest> expectedItemRequests = Arrays.asList(new ItemRequest(), new ItemRequest());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findAllByRequesterId(anyLong())).thenReturn(expectedItemRequests);

        List<ItemRequestDto> actualItemRequests = itemRequestService.getAllItemRequestsByUser(userId);

        assertThat(ItemRequestMapper.INSTANCE.convertItemRequestListToItemRequestDTOList(expectedItemRequests),
                equalTo(actualItemRequests));
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, times(1)).findAllByRequesterId(anyLong());
    }

    @Test
    void getAllItemRequestsByUser_whenUserNotFound_thenExceptionThrown() {
        Long userId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getAllItemRequestsByUser(userId));

        assertThat("Пользователь с идентификатором 0 не найден.", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, never()).findAllByRequesterId(anyLong());
    }

    @Test
    void getAllItemRequestsByOtherUsers_whenInvoked_thenReturnedEmptyList() {
        Long userId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        List<ItemRequestDto> actualItemRequests = itemRequestService
                .getAllItemRequestsByOtherUsers(userId, 0, 1);

        assertThat(actualItemRequests, empty());
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, times(1))
                .findAllByRequesterIdNot(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllItemRequestsByOtherUsers_whenInvoked_thenReturneItemRequestsCollectionInList() {
        Long userId = 0L;
        List<ItemRequest> expectedItemRequests = Arrays.asList(new ItemRequest(), new ItemRequest());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findAllByRequesterIdNot(anyLong(), any(Pageable.class)))
                .thenReturn(expectedItemRequests);

        List<ItemRequestDto> actualItemRequests = itemRequestService
                .getAllItemRequestsByOtherUsers(userId, 0, 1);

        assertThat(ItemRequestMapper.INSTANCE.convertItemRequestListToItemRequestDTOList(expectedItemRequests),
                equalTo(actualItemRequests));
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, times(1))
                .findAllByRequesterIdNot(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllItemRequestsByOtherUsers_whenFromNotValid_thenExceptionThrown() {
        Long userId = 0L;

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> itemRequestService.getAllItemRequestsByOtherUsers(userId, -1, 5));

        assertThat("Параметр from не может быть меньше 0.",
                equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, never()).findById(anyLong());
        inOrder.verify(itemRequestRepository, never())
                .findAllByRequesterIdNot(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllItemRequestsByOtherUsers_whenSizeNotValid_thenExceptionThrown() {
        Long userId = 0L;

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> itemRequestService.getAllItemRequestsByOtherUsers(userId, 0, 0));

        assertThat("Параметр size должен быть положительным.",
                equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, never()).findById(anyLong());
        inOrder.verify(itemRequestRepository, never())
                .findAllByRequesterIdNot(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllItemRequestsByOtherUsers_whenUserNotFound_thenExceptionThrown() {
        Long userId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getAllItemRequestsByOtherUsers(userId, 0, 5));

        assertThat("Пользователь с идентификатором 0 не найден.", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, never())
                .findAllByRequesterIdNot(anyLong(), any(Pageable.class));
    }

    @Test
    void getItemRequestById_whenItemRequestFound_thenReturnedUser() {
        long userId = 0L;
        long itemRequestId = 0L;
        ItemRequest expectedItemRequest = new ItemRequest();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findById(userId)).thenReturn(Optional.of(expectedItemRequest));

        ItemRequestDto actualItemRequest = itemRequestService.getItemRequestById(userId, itemRequestId);

        assertThat(ItemRequestMapper.INSTANCE.toItemRequestDto(expectedItemRequest), equalTo(actualItemRequest));
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, times(1)).findById(itemRequestId);
    }

    @Test
    void getItemRequestById_whenUserNotFound_thenExceptionThrown() {
        long userId = 0L;
        long itemRequestId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getItemRequestById(userId, itemRequestId));

        assertThat("Пользователь с идентификатором 0 не найден.", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, never()).findById(itemRequestId);
    }

    @Test
    void getItemRequestById_whenItemRequestNotFound_thenExceptionThrown() {
        long itemRequestId = 0L;
        long userId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        final ItemRequestNotFoundException exception = assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequestById(userId, itemRequestId));

        assertThat("Запрос с идентификатором 0 не найден.", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    void saveItemRequest_whenUserNotFound_thenExceptionThrown() {
        long userId = 0L;
        ItemRequestDto itemRequestToSave = new ItemRequestDto();
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.createItemRequest(userId, itemRequestToSave));

        assertThat("Пользователь с идентификатором 0 не найден.", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test

    void saveItemRequest_whenItemRequestNotValid_thenExceptionThrown() {
        long userId = 0L;
        ItemRequestDto itemRequestToSave = new ItemRequestDto();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenThrow(new ItemRequestNotSavedException());

        final ItemRequestNotSavedException exception = assertThrows(ItemRequestNotSavedException.class,
                () -> itemRequestService.createItemRequest(userId, itemRequestToSave));

        assertThat("Не удалось сохранить данные запроса", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

}