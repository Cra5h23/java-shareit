package ru.practicum.shareit.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Schema(description = "Имя пользователя", example = "exampleUserName")
    private String name;

    /**
     * Электронная почта пользователя
     */
    @Schema(description = "Электронная почта пользователя", example = "exampleEmail@example.com")
    private String email;
}
