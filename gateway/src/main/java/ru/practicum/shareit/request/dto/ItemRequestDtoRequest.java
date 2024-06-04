package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Dto {@link ItemRequestDtoRequest} использующееся для получения данных из запроса.
 *
 * @author Nikolay Radzivon
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDtoRequest {
    /**
     * Описание вещи.
     */
    @NotBlank(groups = Marker.OnCreate.class, message = "Описание требуемой вещи не может быть пустым")
    @Size(groups = Marker.OnCreate.class, max = 512, message = "Описание требуемой вещи не может быть больше 512 символов")
    private String description;
}
