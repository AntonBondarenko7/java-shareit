package ru.practicum.shareit.request.service;

import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.exception.ItemRequestNotSavedException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.common.utils.ValidPage;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    public List<ItemRequestDto> getAllItemRequestsByUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));

        return ItemRequestMapper.INSTANCE.convertItemRequestListToItemRequestDTOList(
                itemRequestRepository.findAllByRequesterId(userId));
    }

    public List<ItemRequestDto> getAllItemRequestsByOtherUsers(Long userId, Integer from, Integer size) {
        PageRequest page = ValidPage.validate(from, size);
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));

        return ItemRequestMapper.INSTANCE.convertItemRequestListToItemRequestDTOList(
                itemRequestRepository.findAllByRequesterIdNot(userId, page));
    }

    public ItemRequestDto getItemRequestById(Long requestId, Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new ItemRequestNotFoundException(requestId));

        return ItemRequestMapper.INSTANCE.toItemRequestDto(itemRequest);
    }

    @Transactional
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));

        ItemRequest itemRequest = ItemRequestMapper.INSTANCE.toItemRequest(itemRequestDto, user);

        try {
            return ItemRequestMapper.INSTANCE.toItemRequestDto(itemRequestRepository.save(itemRequest));
        } catch (DataIntegrityViolationException e) {
            throw new ItemRequestNotSavedException();
        }

    }

}
