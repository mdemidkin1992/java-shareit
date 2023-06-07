package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.CrudOperations;
import ru.practicum.shareit.util.exception.UserNotFoundException;
import ru.practicum.shareit.util.exception.ValidationException;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends CrudOperations {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserService userService;

    @Test
    public void shouldNotCreateUserWhenEmailAlreadyExists() throws Exception {
        createUser(User.builder().name("Mark").email("mark@email.com").build());
        User newUser = User.builder().name("Bob").email("mark@email.com").build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof ValidationException));
    }

    @Test
    public void shouldGetUserWhenIdIsCorrect() throws Exception {
        User user = User.builder().name("Mark").email("mark@email.com").build();
        UserDto userDto = createUser(user);

        mockMvc.perform(get("/users/{userId}", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andReturn();
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenIdIsIncorrect() throws Exception {
        createUser(User.builder().name("Mark").email("mark@email.com").build());
        long invalidId = 999;

        mockMvc.perform(get("/users/{userId}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserNotFoundException));
    }

    @Test
    public void shouldGetAllUsers() throws Exception {
        createUser(User.builder().name("Mark").email("mark@email.com").build());
        createUser(User.builder().name("Toma").email("toma@email.com").build());

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andReturn();
    }

    @Test
    public void shouldDeleteUserWhenIdIsCorrect() throws Exception {
        UserDto userDto = createUser(User.builder().name("Mark").email("mark@email.com").build());
        long validUserId = userDto.getId();

        mockMvc.perform(delete("/users/{userId}", validUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)))
                .andReturn();
    }

    @Test
    public void shouldNotDeleteUserWhenIdIsIncorrect() throws Exception {
        createUser(User.builder().name("Mark").email("mark@email.com").build());
        long invalidId = 999;

        mockMvc.perform(delete("/users/{userId}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserNotFoundException));
    }

    @Test
    public void shouldUpdateUserWhenValid() throws Exception {
        UserDto userDto = createUser(User.builder().name("Mark").email("mark@email.com").build());

        Map<String, String> fields = new HashMap<>();
        fields.put("name", "New name");
        fields.put("email", "newexample@email.com");

        mockMvc.perform(patch("/users/{userId}", userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fields)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(fields.get("name")))
                .andExpect(jsonPath("$.email").value(fields.get("email")))
                .andReturn();
    }

    @Test
    public void shouldNotUpdateUserWhenIdIsIncorrect() throws Exception {
        long invalidId = 999;

        Map<String, String> fields = new HashMap<>();
        fields.put("name", "New name");
        fields.put("email", "newexample@email.com");

        mockMvc.perform(patch("/users/{userId}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fields)))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserNotFoundException));
    }

    @AfterEach
    public void afterEach() {
        userService.getUsers().forEach(u -> userService.deleteUser(u.getId()));
    }

}