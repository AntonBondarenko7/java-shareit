package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.controller.AdviceController;
import ru.practicum.shareit.common.utils.Constants;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController extends AdviceController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUser(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен список вещей пользователя с id = {}, from = {}, size = {}.",
                userId, from, size);
        return itemClient.getAllItemsByUser(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @PathVariable Long itemId) {
        log.info("Получена вещь с id = {}, userId={}.", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> saveItem(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @Valid @RequestBody ItemDto itemDto) {
        log.info("Добавлена новая вещь: {}, userId={}.", itemDto, userId);
        return itemClient.saveItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        log.info("Обновлена вещь с id = {}:  {}, userId={}.", itemId, itemDto, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItems(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @RequestParam String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен список вещей с текстом: {} пользователя с id = {}, from = {}, size = {}.",
                text, userId, from, size);
        return itemClient.findItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(
            @RequestHeader(Constants.HEADER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto) {
        log.info("Добавлен новый комментарий: {} \n пользователем с id = {} для вещи с id = {}.",
                commentDto, userId, itemId);
        return itemClient.saveComment(userId, itemId, commentDto);
    }

}