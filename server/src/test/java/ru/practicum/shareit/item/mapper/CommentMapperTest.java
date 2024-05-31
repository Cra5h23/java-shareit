package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class CommentMapperTest {

    @Test
    void toCommentTest() {
        var text = CommentRequestDto.builder()
                .text("testComment")
                .build();
        var user = User.builder()
                .id(33L)
                .name("TestUser")
                .email("TestUserEmail@mail.com")
                .build();
        var item = Item.builder()
                .id(22L)
                .owner(User.builder()
                        .id(15L)
                        .name("TestUser15")
                        .email("TestUserEmail15@mail.com")
                        .build())
                .name("Баржа")
                .description("Описание баржи")
                .available(true)
                .build();

        var test = CommentMapper.toComment(text, item, user, TimeZone.getDefault());

        Assertions.assertNotNull(test);
        Assertions.assertEquals("testComment", test.getText());
        Assertions.assertNotNull(test.getCreated());
        Assertions.assertNull(test.getId());
        Assertions.assertEquals(item, test.getItem());
        Assertions.assertEquals(user, test.getAuthor());
    }

    @Test
    void toCommentResponseDtoTest() {
        var user = User.builder()
                .id(33L)
                .name("TestUser")
                .email("TestUserEmail@mail.com")
                .build();
        var item = Item.builder()
                .id(22L)
                .owner(User.builder()
                        .id(15L)
                        .name("TestUser15")
                        .email("TestUserEmail15@mail.com")
                        .build())
                .name("Баржа")
                .description("Описание баржи")
                .available(true)
                .build();

        var comment = Comment.builder()
                .id(1L)
                .author(user)
                .created(ZonedDateTime.of(2024, 3, 12, 10, 12, 13, 0, ZoneId.systemDefault()))
                .text("testText")
                .item(item)
                .build();

        var test = CommentMapper.toCommentResponseDto(comment);

        Assertions.assertNotNull(test);
        Assertions.assertEquals(1, test.getId());
        Assertions.assertEquals("TestUser", test.getAuthorName());
        Assertions.assertEquals(LocalDateTime.of(2024, 3, 12, 10, 12, 13, 0).toString(), test.getCreated());
        Assertions.assertEquals("testText", test.getText());
    }
}