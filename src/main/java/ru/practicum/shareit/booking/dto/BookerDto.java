package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс {@link BookerDto} для поля booker класса {@link BookingResponseDto}.
 *
 * @author Nikolay Radzivon
 * @Date 02.05.2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookerDto {
    /**
     * Идентификационный номер пользователя букера вещи.
     */
    private Long id;
}
