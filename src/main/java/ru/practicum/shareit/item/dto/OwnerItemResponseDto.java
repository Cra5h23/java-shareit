package ru.practicum.shareit.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShort;

import java.util.List;

/**
 * Dto для класса {@link ru.practicum.shareit.item.model.Item} используется в качестве ответа для пользователя владельца вещи.
 *
 * @author Nikolay Radzivon
 * @Date 06.05.2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class OwnerItemResponseDto {
    /**
     * Уникальный идентификационный номер вещи.
     */
    @Schema(description = "Идентификационный номер вещи", example = "1")
    private Long id;

    /**
     * Название вещи.
     */
    @Schema(description = "Название вещи", example = "exampleName")
    private String name;

    /**
     * Описание вещи.
     */
    @Schema(description = "Описание вещи", example = "exampleDescription")
    private String description;

    /**
     * Доступность вещи для аренды.
     */
    @Schema(description = "Доступность для аренды", example = "true")
    private Boolean available;

    /**
     * Предыдущее бронирование вещи.
     */
    private BookingShort lastBooking;

    /**
     * Следующее бронирование вещи.
     */
    private BookingShort nextBooking;

    /**
     * Список комментариев.
     */
    private List<CommentResponseDto> comments;
}
