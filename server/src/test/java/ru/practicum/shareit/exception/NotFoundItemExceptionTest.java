package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class NotFoundItemExceptionTest {
    @Test
    void createNotFoundItemException() {
        var test = new NotFoundItemException("Message");

        Assertions.assertEquals(NotFoundItemException.class, test.getClass());
        Assertions.assertEquals("Message", test.getMessage());
    }
}