package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class NotFoundBookingExceptionTest {
    @Test
    void createNotFoundBookingException() {
        var test = new NotFoundBookingException("Message");

        Assertions.assertEquals(NotFoundBookingException.class, test.getClass());
        Assertions.assertEquals("Message", test.getMessage());
    }
}