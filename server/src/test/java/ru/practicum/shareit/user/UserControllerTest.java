package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsers_whenInvokedDefault_thenResponseStatusOkWithEmptyBody() {
        List<UserDto> response = userController.getAllUsers();

        assertTrue(response.isEmpty());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getAllUsers_whenInvoked_thenResponseStatusOkWithUsersCollectionInBody() {
        List<UserDto> expectedUsers = Arrays.asList(new UserDto());
        when(userService.getAllUsers()).thenReturn(expectedUsers);

        List<UserDto> response = userController.getAllUsers();

        assertThat(expectedUsers, equalTo(response));
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_whenUserFound_thenReturnedUser() {
        long userId = 0L;
        UserDto expectedUser = new UserDto();
        when(userService.getUserById(userId)).thenReturn(expectedUser);

        UserDto response = userController.getUserById(userId);

        assertThat(expectedUser, equalTo(response));
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void saveUser_whenUserValid_thenSavedUser() {
        UserDto expectedUser = new UserDto();
        when(userService.createUser(expectedUser)).thenReturn(expectedUser);

        UserDto response = userController.createUser(expectedUser);

        assertThat(expectedUser, equalTo(response));
        verify(userService, times(1)).createUser(expectedUser);
    }

    @Test
    void updateUser_whenUserValid_thenUpdatedUser() {
        Long userId = 0L;
        UserDto newUser = new UserDto();
        newUser.setName("2");
        newUser.setEmail("2@mail.ru");
        when(userService.updateUser(userId, newUser)).thenReturn(newUser);

        UserDto response = userController.updateUser(userId, newUser);

        assertThat(newUser, equalTo(response));
        verify(userService, times(1)).updateUser(userId, newUser);
    }

}
