package ru.practicum.shareit.item.model;

import lombok.*;

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
    @NotBlank(message = "Название вещи не может быть пустым")
    private String name;
    /**
     * Описание вещи.
     */
    private String description;
    /**
     * Доступность вещи для аренды.
     */
    private Boolean available;
    /**
     * Идентификационный номер пользователя владельца вещи.
     */
    @NotNull
    @Builder.Default
    private Long userId = 0L;
}
