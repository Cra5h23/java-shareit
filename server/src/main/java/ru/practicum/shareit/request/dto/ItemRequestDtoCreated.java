package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Dto для модели {@link ru.practicum.shareit.request.model.ItemRequest} используется для возвращения данных при создании запроса.
 *
 * @author Nikolay Radzivon
 * @Date 19.05.2024
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class ItemRequestDtoCreated {
    /**
     * Идентификационный номер запроса.
     */
    private Long id;

    /**
     * Описание запроса.
     */
    private String description;

    /**
     * Время и дата создания запроса.
     */
    private LocalDateTime created;
}
