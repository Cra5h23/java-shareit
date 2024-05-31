package ru.practicum.shareit.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "Пользователь для ответа", description = "Данные пользователя для ответа")
public class UserResponseDto {
    /**
     * Идентификационный номер пользователя.
     */
    @Schema(description = "Идентификационный номер пользователя", example = "1")
    private Long id;

    /**
     * Имя пользователя.
     */
    @Schema(description = "Имя пользователя", example = "exampleUserName")
    private String name;

    /**
     * Электронная почта пользователя.
     */
    @Schema(description = "Электронная почта пользователя", example = "exampleEmail@example.com")
    private String email;
}
