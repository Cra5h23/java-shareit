package ru.practicum.shareit.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author Nikolay Radzivon
 * @Date 21.04.2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(name = "Пользователь для запроса", description = "Данные пользователя для запроса")
public class UserRequestDto {
    /**
     * Имя пользователя.
     */
    @NotBlank(groups = Marker.OnCreate.class, message = "Поле name не должно быть пустым")
    @Schema(description = "Имя пользователя", example = "exampleUserName")
    private String name;
    /**
     * Электронная почта пользователя
     */
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "Поле email должно иметь формат адреса электронной почты")
    @NotBlank(groups = {Marker.OnCreate.class}, message = "Поле email не должно быть пустым")
    @Schema(description = "Электронная почта пользователя", example = "exampleEmail@example.com")
    private String email;
}
