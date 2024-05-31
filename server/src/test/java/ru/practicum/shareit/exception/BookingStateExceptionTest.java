package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class BookingStateExceptionTest {

    @Test
    void createBookingStateException() {
        var test = new BookingStateException("Message");

        Assertions.assertEquals(BookingStateException.class, test.getClass());
        Assertions.assertEquals("Message", test.getMessage());
    }
}