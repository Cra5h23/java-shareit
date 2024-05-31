package ru.practicum.shareit.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
    @NotBlank(/*groups = Marker.OnCreate.class,*/ message = "Название предмета не может быть пустым")
    @Schema(description = "Название вещи", example = "exampleName")
    private String name;

    /**
     * Описание вещи.
     */
    @NotBlank(/*groups = Marker.OnCreate.class,*/ message = "Описание предмета не может быть пустым")
    @Schema(description = "Описание вещи", example = "exampleDescription")
    private String description;

    /**
     * Возможность аренды вещи.
     */
    @NotNull(/*groups = Marker.OnCreate.class,*/ message = "Возможность аренды должна быть указана")
    @Schema(description = "Доступность для аренды", example = "true")
    private Boolean available;

    /**
     * Идентификационный номер запроса до которого создана вещь.
     */
    private Long requestId;
}
