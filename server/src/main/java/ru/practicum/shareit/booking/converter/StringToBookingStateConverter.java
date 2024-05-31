package ru.practicum.shareit.booking.converter;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.BookingStateException;

/**
 * Класс конвертер для преобразования {@link String} полученной из запроса в объект класса {@link BookingState}.
 *
 * @author Nikolay Radzivon
 * @Date 05.05.2024
 */
public class StringToBookingStateConverter implements Converter<String, BookingState> {
    @Override
    public BookingState convert(String source) {
        try {
            return BookingState.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            String ex = message.substring(message.lastIndexOf(".") + 1);
            throw new BookingStateException(ex);
        }
    }
}
