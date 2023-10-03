package ru.practicum.shareit.item.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.exception.CommentNotSavedException;
import ru.practicum.shareit.utils.ValidPage;
import ru.practicum.shareit.item.exception.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    private final ItemRequestRepository itemRequestRepository;

    public List<ItemDto> getAllItemsByUser(Long userId, Integer from, Integer size) {
        ValidPage.validate(from, size);
        PageRequest page = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));

        return itemRepository.findAllByOwnerId(userId, page)
                .stream()
                .map(item -> getItemById(userId, item.getId()))
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(itemId));

        Booking lastBooking = setLastBooking(userId, item);
        Booking nextBooking = setNextBooking(userId, item);

        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        return ItemMapper.INSTANCE.toItemOwnerDto(item, lastBooking, nextBooking, comments);
    }

    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        validateItemDto(itemDto);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));
        Item item;
        if (itemDto.getRequestId() != null) {
            Optional<ItemRequest> itemRequest = itemRequestRepository.findById(itemDto.getRequestId());
            if (itemRequest.isPresent()) {
                item = ItemMapper.INSTANCE.toItemWithRequest(itemDto, user, itemRequest.get());
            } else {
                item = ItemMapper.INSTANCE.toItem(itemDto, user);
            }
        } else {
            item = ItemMapper.INSTANCE.toItem(itemDto, user);
        }
        try {
            return ItemMapper.INSTANCE.toItemDto(itemRepository.save(item));
        } catch (DataIntegrityViolationException e) {
            throw new ItemNotSavedException();
        }
    }

    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
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
        try {
            return ItemMapper.INSTANCE.toItemDto(itemRepository.saveAndFlush(item));
        } catch (DataIntegrityViolationException e) {
            throw new ItemNotSavedException();
        }

    }

    public List<ItemDto> findItems(Long userId, String text, Integer from, Integer size) {
        if (text.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        PageRequest page = ValidPage.validate(from, size);

        return ItemMapper.INSTANCE.convertItemListToItemDtoList(
                itemRepository.search(text, page));
    }

    @Transactional
    public CommentDto saveComment(Long userId, CommentDto commentDto, Long itemId) {
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

        try {
            return CommentMapper.INSTANCE.toCommentDto(commentRepository.save(comment));
        } catch (DataIntegrityViolationException e) {
            throw new CommentNotSavedException();
        }

    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Ошибка! Статус доступности вещи для аренды " +
                    "не может быть пустым.");
        }
    }

    private Booking setLastBooking(Long userId, Item item) {
        Booking lastBooking;
        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartIsBefore(
                    item.getId(), BookingStatus.APPROVED, LocalDateTime.now(),
                    Sort.by(Sort.Direction.DESC, "end")).orElse(null);
        } else {
            lastBooking = null;
        }
        return lastBooking;
    }

    private Booking setNextBooking(Long userId, Item item) {
        Booking nextBooking;
        if (item.getOwner().getId().equals(userId)) {
            nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartIsAfter(
                    item.getId(), BookingStatus.APPROVED, LocalDateTime.now(),
                    Sort.by(Sort.Direction.ASC, "start")).orElse(null);
        } else {
            nextBooking = null;
        }
        return nextBooking;
    }

}