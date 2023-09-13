package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        return UserMapper.INSTANCE.convertUserListToUserDtoList(userRepository.findAll());
    }

    public UserDto getUserById(Long userId) {
        return UserMapper.INSTANCE.toUserDto(userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(userId)));
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        return UserMapper.INSTANCE.toUserDto(userRepository.save(UserMapper.INSTANCE.toUser(userDto)));
    }

    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(userId));

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        return UserMapper.INSTANCE.toUserDto(userRepository.saveAndFlush(user));
    }

    @Transactional
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }
}
