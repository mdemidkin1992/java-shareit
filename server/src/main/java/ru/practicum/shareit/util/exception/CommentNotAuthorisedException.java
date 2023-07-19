package ru.practicum.shareit.util.exception;

public class CommentNotAuthorisedException extends RuntimeException {
    public CommentNotAuthorisedException(String message) {
        super(message);
    }
}
