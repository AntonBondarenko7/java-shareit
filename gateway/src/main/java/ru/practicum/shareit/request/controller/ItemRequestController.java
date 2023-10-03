package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.controller.AdviceController;
import ru.practicum.shareit.common.utils.Constants;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController extends AdviceController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsByUser(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        log.info("Получен список запросов текущего пользователя вместе с данными об ответах " +
                "на них с id = {}.", userId);
        return itemRequestClient.getAllItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequestsByOtherUsers(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен список запросов пользователя с id = {}, созданных другими, " +
                ", from = {}, size = {}.", userId, from, size);
        return itemRequestClient.getAllItemRequestsByOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @PathVariable Long requestId) {
        log.info("Получен запрос с id = {}, userId={}.", requestId, userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> saveItemRequest(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Добавлен новый запрос на бронирование: {}", itemRequestDto);
        return itemRequestClient.saveItemRequest(userId, itemRequestDto);
    }

}