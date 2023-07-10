package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.request.dto.ItemRequestDescription;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JsonTest
public class ItemRequestDescriptionTest {

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator;

    public ItemRequestDescriptionTest() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    @Test
    @SneakyThrows
    public void testSerialize() {
        ItemRequestDescription itemRequestDescription = new ItemRequestDescription();
        itemRequestDescription.setDescription("Item description");

        String json = objectMapper.writeValueAsString(itemRequestDescription);

        String expectedJson = "{\"description\":\"Item description\"}";
        assertEquals(expectedJson, json);
    }

    @Test
    @SneakyThrows
    public void testDeserialize() {
        String json = "{\"description\":\"Item description\"}";

        ItemRequestDescription itemRequestDescription = objectMapper.readValue(json, ItemRequestDescription.class);

        assertEquals("Item description", itemRequestDescription.getDescription());
    }

    @Test
    public void testValidation() {
        ItemRequestDescription itemRequestDescription = new ItemRequestDescription();
        itemRequestDescription.setDescription("");

        Set<ConstraintViolation<ItemRequestDescription>> violations = validator.validate(itemRequestDescription);
        assertEquals(1, violations.size());

        ConstraintViolation<ItemRequestDescription> notEmptyViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("description"))
                .filter(v -> v.getMessage().equals("must not be empty"))
                .findFirst()
                .orElse(null);
        assertNotNull(notEmptyViolation);
    }
}
