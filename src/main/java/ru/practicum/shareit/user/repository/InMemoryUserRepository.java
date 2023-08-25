package ru.practicum.shareit.user.repository;

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
        User user = UserMapper.toUser(userDto);

        user.setId(generateId());
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public Optional<UserDto> updateUser(Long userId, UserDto userDto) {
        if (!users.containsKey(userId)) {
            return Optional.empty();
        }
        User user = users.get(userId);
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        return Optional.of(UserMapper.toUserDto(user));
    }

    @Override
    public boolean deleteUserById(Long userId) {
        if (users.containsKey(userId)) {
            users.remove(userId);
            return true;
        }
        return false;
    }
}