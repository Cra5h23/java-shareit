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
    @NotBlank(message = "Поле name не должно быть пустым")
    private String name;
    /**
     * Электронная почта пользователя
     */
    @Email(message = "Поле email должно иметь формат адреса электронной почты")
    @NotBlank
    private String email;
}
