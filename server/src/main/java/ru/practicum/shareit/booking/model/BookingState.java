package ru.practicum.shareit.booking.model;

public enum BookingState {
    WAITING("Новое бронирование, ожидает одобрения."),
    APPROVED("Бронирование подтверждено владельцем."),
    REJECTED("Бронирование отклонено владельцем."),
    CANCELED("Бронирование отменено создателем.");

    private final String description;

    BookingState(String description) {
        this.description = description;
    }

}
