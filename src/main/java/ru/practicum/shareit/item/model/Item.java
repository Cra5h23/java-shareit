package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Модель вещь.
 *
 * @author Nikolay Radzivon
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Item {
    /**
     * Уникальный идентификационный номер вещи
     */
    @Builder.Default
    private Long id = 0L;
    /**
     * Имя вещи.
     */
    @NotBlank(groups = Marker.OnCreate.class, message = "Название вещи не может быть пустым")
    private String name;
    /**
     * Описание вещи.
     */
    @NotBlank(groups = Marker.OnCreate.class, message = "Описание предмета не может быть пустым")
    private String description;
    /**
     * Доступность вещи для аренды.
     */
    @NotNull(groups = Marker.OnCreate.class, message = "Возможность аренды должна быть указана")
    private Boolean available;
    /**
     * Идентификационный номер пользователя владельца вещи.
     */
    @NotNull(groups = Marker.OnCreate.class, message = "Пользователь владелец предмета должен быть указан")
    @Builder.Default
    private Long userId = 0L;
}
