package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.exception.UserNotSavedException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers_whenInvokedDefault_thenReturnedEmptyList() {
        List<UserDto> expectedUsers = userService.getAllUsers();

        assertThat(expectedUsers, empty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_whenInvoked_thenReturnedUsersCollectionInList() {
        List<User> expectedUsers = Arrays.asList(new User(), new User());
        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserDto> actualUsers = userService.getAllUsers();

        assertThat(UserMapper.INSTANCE.convertUserListToUserDtoList(expectedUsers),
                equalTo(actualUsers));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_whenUserFound_thenReturnedUser() {
        long userId = 0L;
        User expectedUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        UserDto actualUser = userService.getUserById(userId);

        assertThat(UserMapper.INSTANCE.toUserDto(expectedUser), equalTo(actualUser));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_whenUserNotFound_thenExceptionThrown() {
        long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(userId));

        assertThat("Пользователь с идентификатором 0 не найден.", equalTo(exception.getMessage()));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void saveUser_whenUserValid_thenSavedUser() {
        UserDto userToSave = new UserDto();
        when(userRepository.save(any(User.class))).thenReturn(UserMapper.INSTANCE.toUser(userToSave));

        UserDto actualUser = userService.createUser(userToSave);

        assertThat(userToSave, equalTo(actualUser));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void saveUser_whenUserNotValid_thenExceptionThrown() {
        UserDto userToSave = new UserDto();
        when(userRepository.save(any(User.class)))
                .thenThrow(new UserNotSavedException());

        final UserNotSavedException exception = assertThrows(UserNotSavedException.class,
                () -> userService.createUser(userToSave));

        assertThat("Не удалось сохранить данные пользователя", equalTo(exception.getMessage()));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_whenUserFound_thenUpdatedUser() {
        Long userId = 0L;
        User oldUser = new User();
        oldUser.setName("1");
        oldUser.setEmail("1@mail.ru");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(oldUser));

        User newUser = new User();
        newUser.setName("2");
        newUser.setEmail("1@mail.ru");
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(newUser);

        UserDto actualUser = userService.updateUser(userId, UserMapper.INSTANCE.toUserDto(newUser));

        assertThat(newUser.getName(), equalTo(actualUser.getName()));
        assertThat(newUser.getEmail(), equalTo(actualUser.getEmail()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void updateUser_whenUserNotValid_thenExceptionThrown() {
        Long userId = 0L;
        User oldUser = new User();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(oldUser));
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new UserNotSavedException());

        final UserNotSavedException exception = assertThrows(UserNotSavedException.class,
                () -> userService.updateUser(userId, new UserDto()));

        assertThat("Не удалось сохранить данные пользователя", equalTo(exception.getMessage()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void updateUser_whenUserNotFound_thenExceptionThrown() {
        Long userId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(userId, new UserDto()));

        assertThat("Пользователь с идентификатором 0 не найден.", equalTo(exception.getMessage()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void deleteUser_whenInvoked_thenDeletedUser() {
        Long userId = 0L;

        userService.deleteUserById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

}
