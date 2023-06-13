package ru.practicum.shareit.util.exception;

public class AccessDenyException extends RuntimeException {
    public AccessDenyException(String message) {
        super(message);
    }
}
