package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.exception.UserNotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> userMap = new HashMap<>();
    private final Set<String> emailSet = new HashSet<>();
    private long count = 0;

    @Override
    public UserDto createUser(User user) {
        checkIfEmailExists(user.getEmail());
        user.setId(++count);
        userMap.put(user.getId(), user);
        emailSet.add(user.getEmail());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUserById(long userId) {
        if (userMap.get(userId) == null) {
            String message = String.format("User with id %s not found", userId);
            throw new UserNotFoundException(message);
        }
        return UserMapper.toUserDto(userMap.get(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userMap.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(long userId) {
        getUserById(userId);
        String email = userMap.get(userId).getEmail();
        userMap.remove(userId);
        emailSet.remove(email);
    }

    @Override
    public UserDto updateUser(long userId, Map<String, String> fields) {
        try {
            User targetUser = UserMapper.fromUserDto(getUserById(userId));
            updateUserFields(targetUser, fields);
            userMap.put(targetUser.getId(), targetUser);
        } catch (UserNotFoundException e) {
            String message = String.format("User with id %s not found", userId);
            throw new UserNotFoundException(message);
        }
        return getUserById(userId);
    }

    private void updateUserFields(User user, Map<String, String> fields) {
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            switch (entry.getKey()) {
                case "name":
                    user.setName(entry.getValue());
                    break;
                case "email":
                    checkIfEmailExists(entry.getValue());
                    emailSet.remove(user.getEmail());
                    user.setEmail(entry.getValue());
                    emailSet.add(user.getName());
                    break;
            }
        }
    }

    private void checkIfEmailExists(String email) {
        if (emailSet.contains(email)) {
            String message = String.format("User with email %s already exists.", email);
            throw new ValidationException(message);
        }
    }

}
