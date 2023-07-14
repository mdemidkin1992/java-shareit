package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse createBooking(
            @RequestBody BookingDtoRequest bookingDto,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        log.info("POST request received booking {}", bookingDto);
        BookingDtoResponse response = bookingService.createBooking(bookingDto, userId);
        log.info("Booking created: {}", response);
        return response;
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateBooking(
            @PathVariable long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        log.info("PATCH request received for booking {}", bookingId);
        BookingDtoResponse response = bookingService.updateBooking(bookingId, approved, userId);
        log.info("Booking updated: {}", response);
        return response;
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(
            @PathVariable long bookingId,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        log.info("GET request received for booking {}", bookingId);
        BookingDtoResponse response = bookingService.getBookingById(bookingId, userId);
        log.info("Booking: {}", response);
        return response;
    }

    @GetMapping
    public List<BookingDtoResponse> getBookingsByUserByState(
            @RequestParam String state,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam int from,
            @RequestParam int size
    ) {
        log.info("GET request received for bookings of user {}", userId);
        List<BookingDtoResponse> response = bookingService
                .getBookingsByUserByState(state, userId, from, size);
        log.info("Bookings: {}", response);
        return response;
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getOwnerItemsBooked(
            @RequestParam String state,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam int from,
            @RequestParam int size
    ) {
        log.info("GET request received for item booked of owner {}", userId);
        List<BookingDtoResponse> response = bookingService
                .getOwnerItemsBooked(state, userId, from, size);
        log.info("Bookings: {}", response);
        return response;
    }

}
