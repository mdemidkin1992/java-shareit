package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Test
    void createUser_whenValid_thenReturnUser() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        when(userRepository.save(user)).thenReturn(user);

        UserDto expect = UserMapper.toUserDto(user);
        UserDto actual = userService.createUser(UserMapper.toUserDto(user));
        assertEquals(expect, actual);
    }

    @Test
    void getUsers_whenValid_thenReturnUsersList() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> expect = UserMapper.toUserDto(List.of(user));
        List<UserDto> actual = userService.getUsers();
        assertEquals(expect, actual);
    }

    @Test
    void getUserById_whenValid_thenReturnUser() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto expect = UserMapper.toUserDto(user);
        UserDto actual = userService.getUserById(userId);
        assertEquals(expect, actual);
    }

    @Test
    void getUserById_whenUserNotFound_thenUserNotFoundException() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    void updateUser_whenValid_thenReturnUpdatedUser() {
        User oldUser = new User();
        long oldUserId = 1L;
        oldUser.setId(oldUserId);

        User newUser = new User();
        newUser.setId(oldUserId);

        doNothing().when(userRepository).updateUserFields(newUser, oldUserId);
        when(userRepository.findById(oldUserId)).thenReturn(Optional.of(newUser));

        UserDto expect = UserMapper.toUserDto(oldUser);
        UserDto actual = userService.updateUser(oldUserId, UserMapper.toUserDto(newUser));
        assertEquals(expect, actual);
    }

    @Test
    void updateUser_whenUserNotFound_thenUserNotFoundException() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        User newUser = new User();
        newUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser
                        (
                                userId,
                                UserMapper.toUserDto(newUser)
                        ));
    }

    @Test
    void deleteUser_whenValid_thenDeleteUserFromDb() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        doNothing().when(userRepository).deleteById(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);
    }

    @Test
    void deleteUser_whenUserNotFound_thenUserNotFoundException() {
        User user = new User();
        long userId = 1L;
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(userId));
    }
}