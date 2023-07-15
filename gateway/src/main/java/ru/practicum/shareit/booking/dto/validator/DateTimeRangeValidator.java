package ru.practicum.shareit.booking.dto.validator;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DateTimeRangeValidator implements ConstraintValidator<DateTimeRange, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (!(value instanceof BookingDtoRequest)) {
            throw new IllegalArgumentException("Invalid validation type");
        }

        BookingDtoRequest entity = (BookingDtoRequest) value;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = entity.getStart();
        LocalDateTime end = entity.getEnd();

        return start != null
                && end != null
                && end.isAfter(start)
                && start.isAfter(now);
    }

}
