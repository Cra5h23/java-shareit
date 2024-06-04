package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto для модели {@link ru.practicum.shareit.request.model.ItemRequest} использующееся для получения данных из запроса.
 *
 * @author Nikolay Radzivon
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDtoRequest {
    /**
     * Описание вещи.
     */
    private String description;
}
