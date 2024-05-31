package ru.practicum.shareit.exception;

/**
 * Исключение для конвертера {@link ru.practicum.shareit.booking.converter.StringToBookingStateConverter}
 *
 * @author Nikolay Radzivon
 * @Date 05.05.2024
 */
public class BookingStateException extends RuntimeException {
    public BookingStateException(String message) {
        super(message);
    }
}
