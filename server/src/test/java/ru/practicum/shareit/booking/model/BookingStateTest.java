package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class BookingStateTest {
    @Test
    void valueOfTest() {
        Assertions.assertEquals(BookingState.ALL, BookingState.valueOf("ALL"));
        Assertions.assertEquals(BookingState.WAITING, BookingState.valueOf("WAITING"));
        Assertions.assertEquals(BookingState.CURRENT, BookingState.valueOf("CURRENT"));
        Assertions.assertEquals(BookingState.REJECTED, BookingState.valueOf("REJECTED"));
        Assertions.assertEquals(BookingState.FUTURE, BookingState.valueOf("FUTURE"));
        Assertions.assertEquals(BookingState.PAST, BookingState.valueOf("PAST"));

        Throwable throwable = Assertions.assertThrows(IllegalArgumentException.class, () -> BookingState.valueOf("sdad"));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("No enum constant ru.practicum.shareit.booking.model.BookingState.sdad", throwable.getMessage());
    }
}