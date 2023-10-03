package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.utils.Constants;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllItemsByUser(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                           @RequestParam Integer from,
                                           @RequestParam Integer size) {
        return itemService.getAllItemsByUser(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(Constants.HEADER_USER_ID) Long userId, @PathVariable Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                              @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                              @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> findItems(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                   @RequestParam String text,
                                   @RequestParam(defaultValue = "0") Integer from,
                                   @RequestParam(defaultValue = "10") Integer size) {
        return itemService.findItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                  @RequestBody CommentDto commentDto,
                                  @PathVariable Long itemId) {
        return itemService.saveComment(userId, commentDto, itemId);
    }

}