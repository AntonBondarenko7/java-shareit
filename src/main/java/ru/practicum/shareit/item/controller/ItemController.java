package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.common.Constants;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllItemsByUser(@RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        log.info("Запрос на получение всех вещей для пользователя с id = " + userId);
        List<ItemDto> items = itemService.getAllItemsByUser(userId);
        log.info("Ответ на получение всех вещей для пользователя с id = " + userId + ": " + items);
        return items;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("Запрос на получение информации по вещи с  id = " + itemId);
        ItemDto item = itemService.getItemById(itemId);
        log.info("Ответ на получение информации по вещи с  id = " + itemId + ": " + item);
        return item;
    }

    @PostMapping
    @Validated
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto,
                              @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        log.info("Запрос на создание вещи: " + itemDto + " для пользователя с id = " + userId);
        ItemDto createdItem = itemService.createItem(itemDto, userId);
        log.info("Ответ на создание вещи: " + createdItem);
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                              @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        log.info("Запрос на обновление вещи: " + itemDto + " для пользователя с id = " + userId);
        ItemDto updatedItem = itemService.updateItem(itemId, itemDto, userId);
        log.info("Ответ на обновление вещи: " + updatedItem);
        return updatedItem;
    }

    @GetMapping("/search")
    public List<ItemDto> findItems(@RequestParam String text,
                                                   @RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        log.info("Получен запрос на поиск вещей");
        List<ItemDto> items = itemService.findItems(text, userId);
        log.info("Ответ на поиск вещей: " + items);
        return items;
    }
}