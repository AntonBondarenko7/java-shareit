package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Test
    void getAllItemsByUser_whenInvokedDefault_thenResponseStatusOkWithEmptyBody() {
        Long userId = 0L;
        List<ItemDto> response = itemController.getAllItemsByUser(userId, 0, 0);
        
        assertTrue(response.isEmpty());
        verify(itemService, times(1)).getAllItemsByUser(userId, 0, 0);
    }

    @Test
    void getAllItemsByUser_whenInvoked_thenResponseStatusOkWithItemsCollectionInBody() {
        Long userId = 0L;
        List<ItemDto> expectedItems = Arrays.asList(new ItemDto());
        when(itemService.getAllItemsByUser(userId, 0, 0)).thenReturn(expectedItems);

        List<ItemDto> response = itemController.getAllItemsByUser(userId, 0, 0);
        
        assertThat(expectedItems, equalTo(response));
        verify(itemService, times(1)).getAllItemsByUser(userId, 0, 0);
    }

    @Test
    void getItemById_whenItemFound_thenReturnedItem() {
        long itemId = 0L;
        long userId = 0L;
        ItemDto expectedItem = new ItemDto();
        when(itemService.getItemById(itemId, userId)).thenReturn(expectedItem);

        ItemDto response = itemController.getItemById(itemId, userId);
        
        assertThat(expectedItem, equalTo(response));
        verify(itemService, times(1)).getItemById(itemId, userId);
    }

    @Test
    void createItem_whenItemValid_thenSavedItem() {
        ItemDto expectedItem = new ItemDto();
        long userId = 0L;
        when(itemService.createItem(expectedItem, userId)).thenReturn(expectedItem);

        ItemDto response = itemController.createItem(expectedItem, userId);

        assertThat(expectedItem, equalTo(response));
        verify(itemService, times(1)).createItem(expectedItem, userId);
    }

    @Test
    void updateItem_whenItemValid_thenUpdatedItem() {
        Long itemId = 0L;
        Long userId = 0L;
        ItemDto newItem = new ItemDto();
        newItem.setName("2");
        newItem.setDescription("2");
        newItem.setAvailable(true);
        when(itemService.updateItem(itemId, newItem, userId)).thenReturn(newItem);

        ItemDto response = itemController.updateItem(itemId, newItem, userId);

        assertThat(newItem, equalTo(response));
        verify(itemService, times(1)).updateItem(itemId, newItem, userId);
    }

    @Test
    void findItems_whenInvokedDefault_thenResponseStatusOkWithEmptyBody() {
        Long userId = 0L;
        List<ItemDto> response = itemController.findItems("", userId, 0, 0);

        assertTrue(response.isEmpty());
        verify(itemService, times(1)).findItems("", userId, 0, 0);
    }

    @Test
    void findItems_whenInvoked_thenResponseStatusOkWithItemsCollectionInBody() {
        Long userId = 0L;
        List<ItemDto> expectedItems = Arrays.asList(new ItemDto());
        Mockito.when(itemService.findItems("", userId, 0, 0)).thenReturn(expectedItems);

        List<ItemDto> response = itemController.findItems("", userId, 0, 0);

        assertThat(expectedItems, equalTo(response));
        verify(itemService, times(1)).findItems("", userId, 0, 0);
    }

    @Test
    void saveComment_whenCommentValid_thenSavedComment() {
        CommentDto expectedComment = new CommentDto();
        long itemId = 0L;
        long userId = 0L;
        when(itemService.saveComment(expectedComment, itemId, userId)).thenReturn(expectedComment);

        CommentDto response = itemController.saveComment(expectedComment, itemId, userId);

        assertThat(expectedComment, equalTo(response));
        verify(itemService, times(1)).saveComment(expectedComment, itemId, userId);
    }

}