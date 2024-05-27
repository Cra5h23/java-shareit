package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Nikolay Radzivon
 * @Date 28.05.2024
 */
class QItemResponseTest {

    @Test
    void createTest() {
        var test = new QItemResponse("test");

        Assertions.assertEquals(QItemResponse.class, test.getClass());
    }
}