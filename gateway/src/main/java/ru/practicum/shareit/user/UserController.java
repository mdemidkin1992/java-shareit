package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
@Validated
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@NotNull @RequestBody @Valid UserDto userDto) {
        log.info("POST request received new user: {}", userDto);
        ResponseEntity<Object> response = userClient.createUser(userDto);
        log.info("User created: {}", response.getBody());
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GET request received all users");
        ResponseEntity<Object> response = userClient.getUsers();
        log.info("{}", response);
        return response;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("GET request for user with id {}", userId);
        ResponseEntity<Object> response = userClient.getUser(userId);
        log.info("{}", response);
        return response;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                             @RequestBody UserDto userDto) {
        log.info("PATCH request received user with id: {}", userId);
        ResponseEntity<Object> response = userClient.updateUser(userId, userDto);
        log.info("Updated user: {}", response);
        return response;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        log.info("DELETE request received user with id: {}", userId);
        ResponseEntity<Object> response = userClient.deleteUser(userId);
        log.info("User with id {} deleted", userId);
        return response;
    }

}
