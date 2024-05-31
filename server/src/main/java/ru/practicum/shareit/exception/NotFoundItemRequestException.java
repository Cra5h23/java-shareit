package ru.practicum.shareit.exception;

/**
 * @author Nikolay Radzivon
 * @Date 19.05.2024
 */
public class NotFoundItemRequestException extends RuntimeException {
    public NotFoundItemRequestException(String message) {
        super(message);
    }
}
