package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Dto класса {@link ru.practicum.shareit.request.model.ItemRequest} для передачи данных в качестве ответа.
 *
 * @author Nikolay Radzivon
 * @Date 18.05.2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDtoResponse {
    /**
     * Идентификационный номер запроса.
     */
    private Long id;

    /**
     * Описание запроса.
     */
    private String description;

    /**
     * Дата создания запроса.
     */
    private LocalDateTime created;

    /**
     *Список ответов на запрос.
     */
    private List<ItemFromItemRequest> items;
}
