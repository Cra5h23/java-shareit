package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Nikolay Radzivon
 * @Date 28.05.2024
 */
class QCommentTest {

    @Test
    void createTest() {
        var a = new QComment("asd");

        Assertions.assertEquals(QComment.class, a.getClass());
    }
}