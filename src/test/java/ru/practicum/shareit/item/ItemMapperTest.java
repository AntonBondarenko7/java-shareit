package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

class ItemMapperTest {

    private final ItemMapperImpl itemMapper = new ItemMapperImpl();

    @Test
    void toItemOwnerDto() {
        ItemOwnerDto ItemOwnerDTO = itemMapper
                .toItemOwnerDto(null, null, null, null);

        assertThat(ItemOwnerDTO, nullValue());
    }

    @Test
    void toItem() {
        Item item = itemMapper.toItem(null, null);

        assertThat(item, nullValue());
    }

    @Test
    void toItemWithRequest() {
        Item item = itemMapper.toItemWithRequest(null, null, null);

        assertThat(item, nullValue());
    }

    @Test
    void convertItemListToItemDTOList() {
        List<ItemDto> items = itemMapper.convertItemListToItemDtoList(null);

        assertThat(items, nullValue());
    }

}