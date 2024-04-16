package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для класса {@link ru.practicum.shareit.user.model.User}
 *
 * @author Nikolay Radzivon
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    /**
     * Идентификационный номер пользователя.
     */
    private Long id;
    /**
     * Имя пользователя.
     */
    private String name;
    /**
     * Электронная почта пользователя.
     */
    private String email;
}
