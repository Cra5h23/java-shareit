package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class BookingRequestDto {
    /**
     * Идентификационный номер вещи.
     */
    private Long itemId;

    /**
     * Дата и время начала бронирования.
     */
    private LocalDateTime start;

    /**
     * Дата и время окончания бронирования.
     */
    private LocalDateTime end;
}
