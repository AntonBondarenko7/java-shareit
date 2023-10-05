package ru.practicum.shareit.request;

import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

class ItemRequestMapperTest {

    private final ItemRequestMapperImpl itemRequestMapper = new ItemRequestMapperImpl();

    @Test
    void toItemRequestDto() {
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(null);

        assertThat(itemRequestDto, nullValue());
    }

    @Test
    void toItemRequestDtoWithEmptyItems() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("1");
        itemRequest.setItems(Sets.set(null));

        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);

        assertThat(itemRequest.getId(), equalTo(itemRequestDto.getId()));
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestDto.getItems(), nullValue());
    }

    @Test
    void toItemRequestDtoWithItems() {
        Item item1 = new Item();
        item1.setId(1L);
        Item item2 = new Item();
        item2.setId(2L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("1");
        itemRequest.setCreated(Instant.now());
        itemRequest.setItems(Sets.set(item1, item2));

        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);

        assertThat(itemRequest.getId(), equalTo(itemRequestDto.getId()));
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getCreated(), equalTo(itemRequestDto.getCreated()));
        assertThat(itemRequest.getItems().size(), equalTo(itemRequestDto.getItems().size()));
    }

    @Test
    void toItemRequest() {
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(null, null);

        assertThat(itemRequest, nullValue());
    }

    @Test
    void toItemRequestWithEmptyItems() {
        User user = new User();
        user.setId(5L);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description 1");
        itemRequestDto.setCreated(Instant.now());
        itemRequestDto.setItems(Sets.set(null));

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, user);

        assertThat(itemRequestDto.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(user, equalTo(itemRequest.getRequester()));
        assertThat(itemRequestDto.getCreated(), equalTo(itemRequest.getCreated()));
        assertThat(itemRequest.getItems(), nullValue());
    }


    @Test
    void toItemRequestWithItems() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("1");
        User user = new User();
        user.setId(5L);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description 1");
        itemRequestDto.setCreated(Instant.now());
        itemRequestDto.setItems(Sets.set(itemDto));

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, user);

        assertThat(itemRequestDto.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(user, equalTo(itemRequest.getRequester()));
        assertThat(itemRequestDto.getCreated(), equalTo(itemRequest.getCreated()));
        assertThat(itemRequestDto.getItems().size(), equalTo(itemRequest.getItems().size()));
    }

    @Test
    void convertItemRequestListToItemRequestDTOList() {
        List<ItemRequestDto> itemRequests = itemRequestMapper
                .convertItemRequestListToItemRequestDTOList(null);

        assertThat(itemRequests, nullValue());
    }

}