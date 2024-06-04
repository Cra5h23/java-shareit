package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Dto {@link ItemDtoRequest} для принятия данных вещи из запроса.
 *
 * @author Nikolay Radzivon
 * @Date 20.04.2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemDtoRequest {
    /**
     * Название вещи.
     */
    @NotBlank(groups = Marker.OnCreate.class, message = "Название предмета не может быть пустым")
    private String name;

    /**
     * Описание вещи.
     */
    @NotBlank(groups = Marker.OnCreate.class, message = "Описание предмета не может быть пустым")
    private String description;

    /**
     * Возможность аренды вещи.
     */
    @NotNull(groups = Marker.OnCreate.class, message = "Возможность аренды должна быть указана")
    private Boolean available;

    /**
     * Идентификационный номер запроса для которого создана вещь.
     */
    private Long requestId;
}
