package ru.practicum.shareit.request.controller;

import ru.practicum.shareit.common.utils.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> getAllItemRequestsByUser(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        List<ItemRequestDto> itemRequestDtos = itemRequestService.getAllItemRequestsByUser(userId);
        log.info("Получен список запросов текущего пользователя вместе с данными об ответах " +
                "на них с id = {}, количество = {}.", userId, itemRequestDtos.size());
        return itemRequestDtos;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequestsByOtherUsers(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        List<ItemRequestDto> itemRequestDtos = itemRequestService
                .getAllItemRequestsByOtherUsers(userId, from, size);
        log.info("Получен список запросов пользователя с id = {}, созданных другими, " +
                "количество = {}.", userId, itemRequestDtos.size());
        return itemRequestDtos;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(
            @PathVariable Long requestId,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        ItemRequestDto itemRequestDto = itemRequestService.getItemRequestById(requestId, userId);
        log.info("Получен запрос с id = {}.", requestId);
        return itemRequestDto;
    }

    @PostMapping
    @Validated
    public ItemRequestDto saveItemRequest(
            @Valid @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        itemRequestDto = itemRequestService.createItemRequest(itemRequestDto, userId);
        log.info("Добавлен новый запрос на бронирование: {}", itemRequestDto);
        return itemRequestDto;
    }

}
