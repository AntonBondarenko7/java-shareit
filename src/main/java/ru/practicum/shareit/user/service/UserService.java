package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public UserDto getUserById(Long userId) {
        return userRepository.getUserDtoById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));
    }

    public UserDto createUser(UserDto userDto) {
        return userRepository.createUser(userDto);
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        getUserById(userId);
        return userRepository.updateUser(userId, userDto);
    }

    public boolean deleteUserById(Long userId) {
        return userRepository.deleteUserById(userId);
    }
}
