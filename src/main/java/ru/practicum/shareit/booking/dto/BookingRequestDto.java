package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.validator.EndDateBeforeStartDate;
import ru.practicum.shareit.validator.StartDateEqualsEndDate;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Класс {@link BookingRequestDto} для получения данных бронирования из запроса.
 *
 * @author Nikolay Radzivon
 * @Date 02.05.2024
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@EndDateBeforeStartDate(groups = Marker.OnCreate.class)
@StartDateEqualsEndDate(groups = Marker.OnCreate.class)
public class BookingRequestDto {
    /**
     * Идентификационный номер вещи.
     */
    private Long itemId;

    /**
     * Дата и время начала бронирования.
     */
    @NotNull(groups = Marker.OnCreate.class, message = "Дата начала бронирования не может быть пустой")
    @Future(groups = Marker.OnCreate.class, message = "Дата начала бронирования не должна быть в прошлом")
    private LocalDateTime start;

    /**
     * Дата и время окончания бронирования.
     */
    @NotNull(groups = Marker.OnCreate.class, message = "Дата окончания бронирования не может быть пустой")
    @Future(groups = Marker.OnCreate.class, message = "Дата окончания бронирования не должна быть в прошлом")
    private LocalDateTime end;
}
