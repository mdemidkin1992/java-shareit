package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    public UserDto createUser(User user) {
        return userRepository.createUser(user);
    }

    public List<UserDto> getUsers() {
        return userRepository.getAllUsers();
    }

    public UserDto getUserById(long userId) {
        return userRepository.getUserById(userId);
    }

    public UserDto updateUser(long userId, Map<String, String> fields) {
        return userRepository.updateUser(userId, fields);
    }

    public void deleteUser(long userId) {
        userRepository.deleteUser(userId);
    }

}
