package ru.practicum.shareit.item.model;

import lombok.*;

import ru.practicum.shareit.user.model.User;

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
    private Long id = null;
    /**
     * Имя вещи.
     */

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
     * Пользователь владелец вещи.
     */
    @Builder.Default
    private User owner = null;
}
