package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.exception.UserEmailValidationException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserRepository implements UserRepository {

    Map<Long, User> users = new HashMap<>();
    public static long userId = 0;

    private static Long generateId() {
        return ++userId;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return users.values().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();
    }

    @Override
    public Optional<UserDto> getUserDtoById(Long userId) {
        return users.values().stream()
                .filter(user -> user.getId().equals(userId))
                .map(UserMapper::toUserDto)
                .findFirst();

    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (validateEmail(userDto.getEmail()) > 0) {
            throw new UserEmailValidationException();
        }

        User user = UserMapper.toUser(userDto);

        user.setId(generateId());
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        if (validateIdAndEmail(userId, userDto.getEmail()) != 1) {
            if (validateEmail(userDto.getEmail()) > 0) {
                throw new UserEmailValidationException();
            }
        }

        User user = users.get(userId);

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public boolean deleteUserById(Long userId) {
        if (users.containsKey(userId)) {
            users.remove(userId);
            return true;
        }
        return false;
    }

    private long validateEmail(String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .count();
    }

    private long validateIdAndEmail(Long userId, String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .filter(u -> u.getId().equals(userId))
                .count();
    }
}