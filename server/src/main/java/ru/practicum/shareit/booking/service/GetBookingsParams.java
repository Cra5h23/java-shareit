package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingState;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.TimeZone;

/**
 * Класс {@link GetBookingsParams} для передачи параметров запроса в методы класса {@link ru.practicum.shareit.booking.service.BookingService}
 *
 * @author Nikolay Radzivon
 * @Date 21.05.2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetBookingsParams {
    /**
     * Состояние бронирования.
     */
    @NotNull
    private BookingState state;

    /**
     * Идентификационный номер пользователя от кого запросить список.
     */
    @NotNull
    private Long userId;

    /**
     * Часовой пояс пользователя.
     */
    @NotNull
    private TimeZone timeZone;

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
