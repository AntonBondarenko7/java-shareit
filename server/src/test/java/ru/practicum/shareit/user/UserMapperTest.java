package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

class UserMapperTest {

    private final UserMapperImpl userMapper = new UserMapperImpl();

    @Test
    void toUserDto() {
        UserDto userDto = userMapper.toUserDto(null);

        assertThat(userDto, nullValue());
    }

    @Test
    void toUser() {
        User user = userMapper.toUser(null);

        assertThat(user, nullValue());
    }

    @Test
    void convertUserListToUserDTOList() {
        List<UserDto> users = userMapper.convertUserListToUserDtoList(null);

        assertThat(users, nullValue());
    }

}