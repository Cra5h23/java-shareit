package ru.practicum.shareit.exception;

/**
 * @author Nikolay Radzivon
 * @Date 06.05.2024
 */
public class NotFoundBookingException extends RuntimeException {
    public NotFoundBookingException(String message) {
        super(message);
    }
}
