package ru.practicum.shareit.booking.converter;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.dto.BookingState;

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
            var message = e.getMessage();
            var ex = message.substring(message.lastIndexOf(".") + 1);
            throw new BookingStateException(ex); //todo если что ошибку вынести в отдельный класс
        }
    }

    //todo попробую можно ли так
    public static class BookingStateException extends RuntimeException {
        public BookingStateException(String message) {
            super(message);
        }
    }
}
