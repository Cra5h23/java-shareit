package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Класс {@link ItemSearchParams} используется для передачи параметров запроса в методы класса {@link ru.practicum.shareit.item.service.ItemService}
 *
 * @author Nikolay Radzivon
 * @Date 21.05.2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemSearchParams {
    /**
     * Текст поиска.
     */
    private String text;

    /**
     * Идентификационный номер пользователя.
     */
    @NotNull
    private Long userId;

    /**
     * Индекс первого элемента, начиная с 0.
     */
    @Min(value = 0, message = "Параметр from не может быть меньше 0.")
    private Integer from;

    /**
     * Количество элементов для отображения
     */
    @Min(value = 1, message = "Параметр size не может быть меньше 0.")
    @Max(value = 100, message = "Параметр size не может быть больше 100.")
    private Integer size;
}
