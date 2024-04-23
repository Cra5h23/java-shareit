package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель пользователя.
 *
 * @author Nikolay Radzivon
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * Идентификационный номер пользователя.
     */
    @Builder.Default
    private Long id = null;
    /**
     * Имя пользователя.
     */
    private String name;
    /**
     * Электронная почта пользователя.
     */
    private String email;
}
