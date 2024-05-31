package ru.practicum.shareit.booking.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.BookingStateException;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class StringToBookingStateConverterTest {

    private final StringToBookingStateConverter converter = new StringToBookingStateConverter();

    @Test
    void convertTestValid() {
        var all = converter.convert("all");
        Assertions.assertEquals(BookingState.ALL, all);

        var current = converter.convert("current");
        Assertions.assertEquals(BookingState.CURRENT, current);

        var past = converter.convert("past");
        Assertions.assertEquals(BookingState.PAST, past);

        var future = converter.convert("future");
        Assertions.assertEquals(BookingState.FUTURE, future);

        var waiting = converter.convert("waiting");
        Assertions.assertEquals(BookingState.WAITING, waiting);

        var rejected = converter.convert("rejected");
        Assertions.assertEquals(BookingState.REJECTED, rejected);
    }

    @Test
    void convertTestNotValid() {
        var source = "asdfg";

        Throwable throwable = Assertions.assertThrows(BookingStateException.class, () -> converter.convert(source));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("ASDFG", throwable.getMessage());
    }
}