package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto для класса {@link ru.practicum.shareit.item.model.Item} использующееся в качестве ответов на запрос в классе {@link ItemRequestDtoResponse}.
 *
 * @author Nikolay Radzivon
 * @Date 19.05.2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemFromItemRequest {
    /**
     * Идентификационный номер предмета.
     */
    private Long id;

    /**
     * Название предмета.
     */
    private String name;

    /**
     * Описание предмета.
     */
    private String description;

    /**
     * Идентификационный номер запроса.
     */
    private Long requestId;

    /**
     * Доступность предмета для аренды.
     */
    private Boolean available;
}
