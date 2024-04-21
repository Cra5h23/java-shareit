package ru.practicum.shareit.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "Вещь для ответа", description = "Сущность вещи для ответа")
public class ItemResponseDto {
    /**
     * Уникальный идентификационный номер вещи.
     */
    @Schema(description = "Идентификационный номер вещи", example = "1")
    private Long id;
    /**
     * Название вещи.
     */
    @Schema(description = "Название вещи",example = "exampleName")
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
}
