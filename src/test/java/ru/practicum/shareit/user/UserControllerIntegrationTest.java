package ru.practicum.shareit.user;

import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.exception.UserNotSavedException;

import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    private final UserDto userDto = new UserDto();
    private final UserDto userDto2 = new UserDto();

    @BeforeEach
    public void addItems() {
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("mail@mail.ru");

        userDto2.setId(2L);
        userDto2.setName("name 2");
        userDto2.setEmail("mail2@mail.ru");
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenInvoked_thenResponseStatusOkWithUsersCollectionInBody() {
        List<UserDto> users = Arrays.asList(userDto, userDto2);
        when(userService.getAllUsers()).thenReturn(users);

        String result = mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(users), equalTo(result));
        verify(userService, times(1)).getAllUsers();
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenInvoked_thenResponseStatusOkWithEmptyList() {
        String result = mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(Collections.EMPTY_LIST), equalTo(result));
        verify(userService, times(1)).getAllUsers();
    }

    @SneakyThrows
    @Test
    void getUserById_whenUserFound_thenReturnedUser() {
        long userId = 0L;
        when(userService.getUserById(anyLong())).thenReturn(userDto);

        String result = mockMvc.perform(get("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(userDto), equalTo(result));
        verify(userService, times(1)).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void getUserById_whenUserNotFound_thenReturnedNotFound() {
        long userId = 0L;
        UserNotFoundException exception = new UserNotFoundException(userId);
        when(userService.getUserById(anyLong()))
                .thenThrow(exception);

        String result = mockMvc.perform(get("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat("Ошибка: Пользователь с идентификатором 0 не найден.", equalTo(result));
        verify(userService, times(1)).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void saveUser_whenUserValid_thenSavedUser() {
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(userDto), equalTo(result));
        verify(userService, times(1)).createUser(userDto);
    }

    @SneakyThrows
    @Test
    void saveUser_whenUserNotSaves_thenReturnedConflict() {
        UserNotSavedException exception = new UserNotSavedException();
        when(userService.createUser(any(UserDto.class))).thenThrow(exception);

        String result = mockMvc.perform(post("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat("Ошибка: Не удалось сохранить данные пользователя", equalTo(result));
        verify(userService, times(1)).createUser(userDto);
    }

//    @SneakyThrows
//    @Test
//    void saveUser_whenUserNotSaves_thenReturnedBadRequest() {
//        userDto.setEmail(null);
//
//        String result = mockMvc.perform(post("/users")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(userDto)))
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andReturn()
//                .getResponse()
//                .getContentAsString(StandardCharsets.UTF_8);
//
//        assertThat("email не может быть пустым",
//                equalTo(result));
//        verify(userService, never()).createUser(userDto);
//    }

    @SneakyThrows
    @Test
    void updateUser_whenUserValid_thenUpdatedUser() {
        long userId = 0L;
        when(userService.updateUser(anyLong(), any(UserDto.class))).thenReturn(userDto2);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto2)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(userDto2), equalTo(result));
        verify(userService, times(1)).updateUser(userId, userDto2);
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserNotFound_thenReturnedConflict() {
        long userId = 0L;
        UserNotSavedException exception = new UserNotSavedException();
        when(userService.updateUser(anyLong(), any(UserDto.class))).thenThrow(exception);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto2)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat("Ошибка: Не удалось сохранить данные пользователя", equalTo(result));
        verify(userService, times(1)).updateUser(userId, userDto2);
    }

    @SneakyThrows
    @Test
    void deleteUserById_whenInvoked_thenDeletedUser() {
        long userId = 0L;

        String result = mockMvc.perform(delete("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(result, blankString());
        verify(userService, times(1)).deleteUserById(userId);
    }

}
