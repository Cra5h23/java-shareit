package ru.practicum.shareit.booking.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.BookingStateException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class StringToBookingStateConverterTest {

    private StringToBookingStateConverter converter = new StringToBookingStateConverter();

    @Test
    void convertTestValid() {
        var all = converter.convert("all");
        Assertions.assertEquals(all, BookingState.ALL);

        var current = converter.convert("current");
        Assertions.assertEquals(current, BookingState.CURRENT);

        var past = converter.convert("past");
        Assertions.assertEquals(past, BookingState.PAST);

        var future = converter.convert("future");
        Assertions.assertEquals(future, BookingState.FUTURE);

        var waiting = converter.convert("waiting");
        Assertions.assertEquals(waiting, BookingState.WAITING);

        var rejected = converter.convert("rejected");
        Assertions.assertEquals(rejected, BookingState.REJECTED);
    }

    @Test
    void convertTestNotValid() {
        var source = "asdfg";

        Throwable throwable = Assertions.assertThrows(BookingStateException.class,()-> converter.convert(source));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("ASDFG", throwable.getMessage());
    }
}