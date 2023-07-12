package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator;

    public UserDtoJsonTest() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    @Test
    @SneakyThrows
    void testUserDto() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@email.com")
                .build();

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("email@email.com");
    }

    @Test
    @SneakyThrows
    public void testSerialize() {
        UserDto userDto = UserDto.builder()
                .id(12345)
                .name("John Doe")
                .email("johndoe@example.com")
                .build();

        String json = objectMapper.writeValueAsString(userDto);

        String expectedJson = "{\"id\":12345,\"name\":\"John Doe\",\"email\":\"johndoe@example.com\"}";
        assertEquals(expectedJson, json);
    }

    @Test
    @SneakyThrows
    public void testDeserialize() {
        String json = "{\"id\":12345,\"name\":\"John Doe\",\"email\":\"johndoe@example.com\"}";

        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        assertEquals(12345, userDto.getId());
        assertEquals("John Doe", userDto.getName());
        assertEquals("johndoe@example.com", userDto.getEmail());
    }

    @Test
    public void testEmailValidation() {
        UserDto userDto = UserDto.builder()
                .id(12345)
                .name("John Doe")
                .email("invalidemail")
                .build();

        var violations = validator.validate(userDto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("User email is of invalid format")));
    }

}