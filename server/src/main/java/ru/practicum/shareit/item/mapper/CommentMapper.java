package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * Маппер для классов {@link Comment}, {@link CommentResponseDto} и {@link CommentRequestDto}.
 * Предназначен для преобразования объекта класса {@link CommentRequestDto} в объект класса {@link Comment}
 * и объект класса {@link Comment} в объект класса {@link CommentResponseDto}.
 *
 * @author Nikolay Radzivon
 * @Date 07.05.2024
 */
public class CommentMapper {

    public static Comment toComment(CommentRequestDto text, Item item, User user, TimeZone timeZone) {
        return Comment.builder()
                .author(user)
                .item(item)
                .created(LocalDateTime.now().atZone(timeZone.toZoneId()))
                .text(text.getText())
                .build();
    }

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated().toLocalDateTime())
                .build();
    }
}
