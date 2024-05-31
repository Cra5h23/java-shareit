package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class NotFoundItemRequestExceptionTest {
    @Test
    void createNotFoundItemRequestException() {
        var test = new NotFoundItemRequestException("Message");

        Assertions.assertEquals(NotFoundItemRequestException.class, test.getClass());
        Assertions.assertEquals("Message", test.getMessage());
    }
}