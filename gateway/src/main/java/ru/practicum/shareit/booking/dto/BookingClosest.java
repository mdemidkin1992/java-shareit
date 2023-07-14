package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookingClosest {
    private final Long id;
    private final Long bookerId;

    public BookingClosest(Long id, Long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }
}
