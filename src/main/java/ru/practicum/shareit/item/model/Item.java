package ru.practicum.shareit.item.model;

import lombok.Data;

/**
 * Класс вещь
 *
 * @author Nikolay Radzivon
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Item {
    /**
     * Уникальный идентификационный номер вещи.
     */
    private Long id;
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
     * Идентификационный номер пользователя владельца вещи.
     */
    private Long userId;
}
