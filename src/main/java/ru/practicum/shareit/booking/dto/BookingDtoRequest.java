package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import ru.practicum.shareit.booking.dto.deserialiser.LocalDateTimeDeserializer;
import ru.practicum.shareit.booking.dto.validator.DateTimeRange;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@DateTimeRange
public class BookingDtoRequest {
    @NotNull
    private long itemId;
    @NotNull
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime start;
    @NotNull
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime end;
}
