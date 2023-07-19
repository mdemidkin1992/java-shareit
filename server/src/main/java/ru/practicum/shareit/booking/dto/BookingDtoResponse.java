package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@Builder
public class BookingDtoResponse {
    private long id;
    private String start;
    private String end;
    private String status;
    private UserDto booker;
    private ItemDto item;
}
