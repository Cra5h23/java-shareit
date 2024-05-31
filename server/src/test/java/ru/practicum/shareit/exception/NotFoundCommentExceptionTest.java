package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class NotFoundCommentExceptionTest {
    @Test
    void createNotFoundCommentException() {
        var test = new NotFoundCommentException("Message");

        Assertions.assertEquals(NotFoundCommentException.class, test.getClass());
        Assertions.assertEquals("Message", test.getMessage());
    }
}