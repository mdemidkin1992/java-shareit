package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto createUser(UserDto userDto) {
        return userRepository.createUser(userDto);
    }

    public List<UserDto> getUsers() {
        return userRepository.getAllUsers();
    }

    public UserDto getUserById(long userId) {
        return userRepository.getUserById(userId);
    }

    public UserDto updateUser(long userId, UserDto userDto) {
        return userRepository.updateUser(userId, userDto);
    }

    public void deleteUser(long userId) {
        userRepository.deleteUser(userId);
    }

}
