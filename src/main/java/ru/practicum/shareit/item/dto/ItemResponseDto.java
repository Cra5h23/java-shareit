package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель для возвращения данных о предмете в ответ.
 *
 * @author Nikolay Radzivon
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemResponseDto {
    /**
     * Уникальный идентификационный номер вещи.
     */
    private Long id;
    /**
     * Название вещи.
     */
    private String name;
    /**
     * Описание вещи.
     */
    private String description;
    /**
     * Доступность вещи для аренды.
     */
    private Boolean available;
}
