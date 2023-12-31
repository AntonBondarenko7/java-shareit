package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingOwnerDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import java.util.Arrays;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemOwnerDtoTest {

    @Autowired
    private JacksonTester<ItemOwnerDto> json;

    @Test
    void testItemOwnerDto() throws Exception {
        BookingOwnerDto lastBooking = new BookingOwnerDto();
        lastBooking.setId(3L);
        lastBooking.setBookerId(2L);
        BookingOwnerDto nextBooking = new BookingOwnerDto();
        nextBooking.setId(5L);
        nextBooking.setBookerId(2L);
        CommentDto commentDto1 = new CommentDto();
        commentDto1.setId(6L);
        CommentDto commentDto2 = new CommentDto();
        commentDto2.setId(8L);

        ItemOwnerDto itemDto = new ItemOwnerDto();
        itemDto.setId(1L);
        itemDto.setName("item 1");
        itemDto.setDescription("description 1");
        itemDto.setAvailable(true);
        itemDto.setRequestId(2L);
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(Arrays.asList(commentDto1, commentDto2));

        JsonContent<ItemOwnerDto> result = json.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.comments");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDto.getAvailable().booleanValue());
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemDto.getRequestId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(itemDto.getLastBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(itemDto.getLastBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(itemDto.getNextBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo(itemDto.getNextBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id")
                .isEqualTo(itemDto.getComments().get(0).getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.comments[1].id")
                .isEqualTo(itemDto.getComments().get(1).getId().intValue());
    }

}