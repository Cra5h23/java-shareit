package ru.practicum.shareit.booking.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class BookingServiceExceptionTest {
    @Test
    void CreateException() {
        var test = new BookingServiceException("Message");

        Assertions.assertNotNull(test);
        Assertions.assertEquals("Message", test.getMessage());
    }
}