package ru.practicum.shareit.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto класса {@link ru.practicum.shareit.item.model.Item} для принятия данных вещи из запроса.
 *
 * @author Nikolay Radzivon
 * @Date 20.04.2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Schema(name = "Вещь для запроса", description = "Сущность вещи для запроса")
public class ItemDtoRequest {
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
     * Возможность аренды вещи.
     */
    @Schema(description = "Доступность для аренды", example = "true")
    private Boolean available;

    /**
     * Идентификационный номер запроса до которого создана вещь.
     */
    private Long requestId;
}
