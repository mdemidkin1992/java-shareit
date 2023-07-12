package ru.practicum.shareit.booking.model;

public enum StatusType {
    WAITING("Новое бронирование, ожидает одобрения."),
    APPROVED("Бронирование подтверждено владельцем."),
    REJECTED("Бронирование отклонено владельцем."),
    CANCELED("Бронирование отменено создателем.");

    public final String description;

    StatusType(String description) {
        this.description = description;
    }

}
