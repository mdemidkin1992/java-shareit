package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {

    BookingDtoResponse createBooking(BookingDtoRequest bookingDto, long userId);

    BookingDtoResponse updateBooking(long bookingId, Boolean approved, long userId);

    BookingDtoResponse getBookingById(long bookingId, long userId);

    List<BookingDtoResponse> getBookingsByUserByState(String state, long userId, int from, int size);

    List<BookingDtoResponse> getOwnerItemsBooked(String state, long userId, int from, int size);

}
