package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingState {
	WAITING("Новое бронирование, ожидает одобрения."),
	APPROVED("Бронирование подтверждено владельцем."),
	REJECTED("Бронирование отклонено владельцем."),
	CANCELED("Бронирование отменено создателем.");

	public final String description;

	BookingState(String description) {
		this.description = description;
	}

	public static Optional<BookingState> from(String stringState) {
		for (BookingState state : values()) {
			if (state.name().equalsIgnoreCase(stringState)) {
				return Optional.of(state);
			}
		}
		return Optional.empty();
	}
}
