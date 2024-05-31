package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;

/**
 * Класс {@link BookingResponseDto} для отдачи данных бронирования в качестве ответа.
 *
 * @author Nikolay Radzivon.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class BookingResponseDto {
    /**
     * Идентификационный номер бронирования.
     */
    private Long id;

    /**
     * Дата и время начала бронирования.
     */
    private String start;

    /**
     * Дата и время окончания бронирования.
     */
    private String end;

    /**
     * Статус бронирования.
     */
    private BookingStatus status;

    /**
     * Пользователь бронирующий вещь.
     */
    private BookerDto booker;

    /**
     * Бронируемая вещь.
     */
    private ItemBookingDto item;
}
