package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class BookingStatusTest {

    @Test
    void valueOfTest() {
        Assertions.assertEquals(BookingStatus.APPROVED, BookingStatus.valueOf("APPROVED"));
        Assertions.assertEquals(BookingStatus.WAITING, BookingStatus.valueOf("WAITING"));
        Assertions.assertEquals(BookingStatus.CANCELED, BookingStatus.valueOf("CANCELED"));
        Assertions.assertEquals(BookingStatus.REJECTED, BookingStatus.valueOf("REJECTED"));

        Throwable throwable = Assertions.assertThrows(IllegalArgumentException.class, () -> BookingStatus.valueOf("sdad"));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("No enum constant ru.practicum.shareit.booking.model.BookingStatus.sdad", throwable.getMessage());
    }
}