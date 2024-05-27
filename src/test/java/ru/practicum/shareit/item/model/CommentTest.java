package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class CommentTest {
    @Test
    void createCommentTest() {
        var comment = Comment.builder()
                .text("test")
                .created(ZonedDateTime.of(2024, 10, 3, 12, 3, 0, 0, ZoneId.systemDefault()))
                .author(User.builder()
                        .id(1L)
                        .email("testEmail@mail.com")
                        .name("testName")
                        .build())
                .id(1L)
                .item(Item.builder()
                        .owner(User.builder()
                                .id(2L)
                                .email("testEmail2@mail.com")
                                .name("test2Name")
                                .build())
                        .name("testItem")
                        .description("testDescription")
                        .available(true)
                        .build())
                .build();

        int i = comment.hashCode();
        Assertions.assertNotNull(i);
    }

    @Test
    void createCommentTestEquals() {
        var comment = Comment.builder()
                .text("test")
                .created(ZonedDateTime.of(2024, 10, 3, 12, 3, 0, 0, ZoneId.systemDefault()))
                .author(User.builder()
                        .id(1L)
                        .email("testEmail@mail.com")
                        .name("testName")
                        .build())
                .id(1L)
                .item(Item.builder()
                        .owner(User.builder()
                                .id(2L)
                                .email("testEmail2@mail.com")
                                .name("test2Name")
                                .build())
                        .name("testItem")
                        .description("testDescription")
                        .available(true)
                        .build())
                .build();

        var comment2 = Comment.builder()
                .text("test")
                .created(ZonedDateTime.of(2024, 10, 3, 12, 3, 0, 0, ZoneId.systemDefault()))
                .author(User.builder()
                        .id(1L)
                        .email("testEmail@mail.com")
                        .name("testName")
                        .build())
                .id(1L)
                .item(Item.builder()
                        .owner(User.builder()
                                .id(2L)
                                .email("testEmail2@mail.com")
                                .name("test2Name")
                                .build())
                        .name("testItem")
                        .description("testDescription")
                        .available(true)
                        .build())
                .build();

        int i = comment.hashCode();
        Assertions.assertEquals(comment, comment2);
    }
}