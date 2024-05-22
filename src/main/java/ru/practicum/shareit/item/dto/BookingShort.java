package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс {@link BookingShort} для полей lastBooking и nextBooking класса {@link ru.practicum.shareit.item.dto.OwnerItemResponseDto}
 *
 * @author Nikolay Radzivon
 * @Date 06.05.2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BookingShort {
    /**
     * Идентификационный номер бронирования.
     */
    private Long id;

    /**
     * Идентификационный номер пользователя букера.
     */
    private Long bookerId;
}
