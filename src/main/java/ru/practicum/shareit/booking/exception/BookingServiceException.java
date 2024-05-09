package ru.practicum.shareit.booking.exception;

/**
 * Класс {@link BookingServiceException} для исключений класса {@link ru.practicum.shareit.booking.service.BookingService}.
 *
 * @author Nikolay Radzivon
 * @Date 02.05.2024
 */
public class BookingServiceException extends RuntimeException {
    public BookingServiceException(String message) {
        super(message);
    }
}
