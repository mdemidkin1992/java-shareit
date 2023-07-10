package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JsonTest
public class BookingDtoRequestJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator;

    public BookingDtoRequestJsonTest() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    @Test
    @SneakyThrows
    public void testSerialize() {
        BookingDtoRequest bookingDto = new BookingDtoRequest();
        bookingDto.setItemId(123);
        bookingDto.setStart(LocalDateTime.of(2023, 7, 7, 10, 0));
        bookingDto.setEnd(LocalDateTime.of(2023, 7, 7, 12, 0));

        String json = objectMapper.writeValueAsString(bookingDto);

        String expectedJson = "{\"itemId\":123,\"start\":\"2023-07-07T10:00:00\",\"end\":\"2023-07-07T12:00:00\"}";
        assertEquals(expectedJson, json);
    }

    @Test
    @SneakyThrows
    public void testDeserialize() {
        String json = "{\"itemId\":123,\"start\":\"2023-07-07T10:00:00\",\"end\":\"2023-07-07T12:00:00\"}";

        BookingDtoRequest bookingDto = objectMapper.readValue(json, BookingDtoRequest.class);

        assertEquals(123, bookingDto.getItemId());
        assertEquals(LocalDateTime.of(2023, 7, 7, 10, 0), bookingDto.getStart());
        assertEquals(LocalDateTime.of(2023, 7, 7, 12, 0), bookingDto.getEnd());
    }

    @Test
    public void testValidation() {
        BookingDtoRequest bookingDto = new BookingDtoRequest();
        bookingDto.setItemId(123);
        bookingDto.setStart(null);
        bookingDto.setEnd(null);

        Set<ConstraintViolation<BookingDtoRequest>> violations = validator.validate(bookingDto);
        assertEquals(3, violations.size());

        ConstraintViolation<BookingDtoRequest> startViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("start"))
                .findFirst()
                .orElse(null);
        assertNotNull(startViolation);
        assertEquals("must not be null", startViolation.getMessage());

        ConstraintViolation<BookingDtoRequest> endViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("end"))
                .findFirst()
                .orElse(null);
        assertNotNull(endViolation);
        assertEquals("must not be null", endViolation.getMessage());
    }
}
