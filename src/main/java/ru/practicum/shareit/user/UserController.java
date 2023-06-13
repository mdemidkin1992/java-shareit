package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@NotNull @RequestBody @Valid UserDto userDto) {
        log.info("POST request received new user: {}", userDto);
        UserDto response = userService.createUser(userDto);
        log.info("User created: {}", response);
        return response;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("GET request received all users");
        List<UserDto> response = userService.getUsers();
        log.info("Total number of users: {}", response.size());
        log.info("{}", response);
        return response;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("GET request for user with id {}", userId);
        UserDto response = userService.getUserById(userId);
        log.info("{}", response);
        return response;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId,
                              @RequestBody UserDto userDto) {
        log.info("PATCH request received user with id: {}", userId);
        UserDto response = userService.updateUser(userId, userDto);
        log.info("Updated user: {}", response);
        return response;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("DELETE request received user with id: {}", userId);
        userService.deleteUser(userId);
        log.info("User with id {} deleted", userId);
    }

}
