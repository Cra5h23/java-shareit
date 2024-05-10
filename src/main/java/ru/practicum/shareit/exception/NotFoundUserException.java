package ru.practicum.shareit.exception;

/**
 * @author Nikolay Radzivon
 * @Date 03.05.2024
 */
public class NotFoundUserException extends RuntimeException {
    public NotFoundUserException(String message) {
        super(message);
    }
}
