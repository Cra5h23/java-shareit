package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

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
    private Long id;
    /**
     * Имя пользователя.
     */
    @NotBlank(groups = Marker.OnCreate.class, message = "Поле name не должно быть пустым")
    private String name;
    /**
     * Электронная почта пользователя.
     */
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Поле email должно иметь формат адреса электронной почты")
    @NotBlank(groups = {Marker.OnCreate.class}, message = "Поле email не должно быть пустым")
    private String email;
}
