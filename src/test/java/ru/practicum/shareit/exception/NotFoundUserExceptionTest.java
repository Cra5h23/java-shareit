package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class NotFoundUserExceptionTest {
    @Test
    void createNotFoundUserException() {
        var test = new NotFoundUserException("Message");

        Assertions.assertEquals(NotFoundUserException.class, test.getClass());
        Assertions.assertEquals("Message", test.getMessage());
    }
}