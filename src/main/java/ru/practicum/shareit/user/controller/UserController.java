package ru.practicum.shareit.user.controller;


import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.common.controller.AdviceController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController extends AdviceController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрос на поиск всех пользователей");
        List<UserDto> users = userService.getAllUsers();
        log.info("Ответ на поиск всех пользователей: " + users);
        return users;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("Запрос на получение пользователя с id = " + userId);
        UserDto user = userService.getUserById(userId);
        log.info("Ответ на получение пользователя с id = " + userId + ": " + user);
        return user;
    }

    @PostMapping
    @Validated
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Запрос на создание пользователя: " + userDto);
        UserDto createdUser = userService.createUser(userDto);
        log.info("Ответ на создание пользователя: " + createdUser);
        return createdUser;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Запрос на обновление пользователя: " + userDto);
        UserDto updatedUser = userService.updateUser(userId, userDto);
        log.info("Ответ на обновление пользователя: " + updatedUser);
        return updatedUser;
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Запрос на удаление пользователя c id = " + userId);
        userService.deleteUserById(userId);
        log.info("Удален пользователь c id = " + userId);
    }

}
