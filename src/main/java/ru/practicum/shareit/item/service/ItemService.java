package ru.practicum.shareit.item.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.exception.*;
import ru.practicum.shareit.common.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public List<ItemDto> getAllItemsByUser(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId, Sort.by(Sort.Direction.ASC, "id"));
        return items.stream()
                .map(item -> getItemById(item.getId(), userId))
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(itemId));

        Booking lastBooking;
        Booking nextBooking;
        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartIsBefore(
                    itemId, BookingStatus.APPROVED, LocalDateTime.now(),
                    Sort.by(Sort.Direction.DESC, "end")).orElse(null);
            nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartIsAfter(
                    itemId, BookingStatus.APPROVED, LocalDateTime.now(),
                    Sort.by(Sort.Direction.ASC, "start")).orElse(null);
        } else {
            lastBooking = null;
            nextBooking = null;
        }
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        return ItemMapper.INSTANCE.toItemDto(item);
    }

    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        validateItemDto(itemDto);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));
        Item item = ItemMapper.INSTANCE.toItem(itemDto, user);

        return ItemMapper.INSTANCE.toItemDto(itemRepository.save(item));
    }

    @Transactional
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(itemId));
        if (!item.getOwner().getId().equals(userId)) {
            throw new ItemOwnershipException(userId, itemId);
        }
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.INSTANCE.toItemDto(itemRepository.saveAndFlush(item));
    }

    public List<ItemDto> findItems(String text, Long userId) {
        if (text.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return ItemMapper.INSTANCE.convertItemListToItemDtoList(itemRepository.search(text));
    }

    @Transactional
    public CommentDto saveComment(CommentDto commentDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(itemId));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));
        if (bookingRepository.isFindBooking(itemId, userId, LocalDateTime.now()) == null) {
            throw new ValidationException("Ошибка!  Отзыв может оставить только тот пользователь, " +
                    "который брал эту вещь в аренду, и только после окончания срока аренды.");
        }

        Comment comment = CommentMapper.INSTANCE.toComment(commentDto, item, user);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.INSTANCE.toCommentDto(commentRepository.save(comment));
    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Ошибка! Статус доступности вещи для аренды " +
                    "не может быть пустым.");
        }
    }

}