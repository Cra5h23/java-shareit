package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Модель пользователя.
 *
 * @author Nikolay Radzivon
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * Имя пользователя.
     */
    @NotBlank
    private String name;
    /**
     * Электронная почта пользователя
     */
    @Email
    @NotBlank
    private String email;
}
