package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Dto для класса {@link ru.practicum.shareit.item.model.Comment} для данных комментариев получаемых из запроса.
 *
 * @author Nikolay Radzivon
 * @Date 07.05.2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CommentRequestDto {
    /**
     * Текст комментария.
     */
    @NotBlank(/*groups = Marker.OnCreate.class*/)
    @Size(max = 512, groups = Marker.OnCreate.class)
    private String text;
}
