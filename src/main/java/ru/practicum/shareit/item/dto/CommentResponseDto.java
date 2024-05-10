package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto для класса {@link ru.practicum.shareit.item.model.Comment} используется в качестве ответа.
 *
 * @author Nikolay Radzivon
 * @Date 07.05.2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CommentResponseDto {
    /**
     * Идентификационный номер комментария.
     */
    private Long id;

    /**
     * Текст комментария.
     */
    private String text;

    /**
     * Имя автора.
     */
    private String authorName;

    /**
     * Время создания комментария.
     */
    private String created;
}
