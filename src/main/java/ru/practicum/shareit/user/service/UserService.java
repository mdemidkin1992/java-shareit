package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.fromUserDto(userDto));
        return UserMapper.toUserDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.toUserDto(users);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(String.format("User with id %s not found", userId)));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    public UserDto updateUser(long userId, UserDto userDto) {
        userRepository.updateUserFields(UserMapper.fromUserDto(userDto), userId);
        return UserMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", userId))));
    }

    @Transactional
    public void deleteUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", userId)));
        userRepository.deleteById(userId);
    }

}

