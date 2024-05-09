package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс {@link ItemBookingDto} для поля item класса {@link BookingResponseDto}.
 *
 * @author Nikolay Radzivon
 * @Date 02.05.2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemBookingDto {
    /**
     * Идентификационный номер вещи.
     */
    private Long id;
    /**
     * Название вещи.
     */
    private String name;
}
