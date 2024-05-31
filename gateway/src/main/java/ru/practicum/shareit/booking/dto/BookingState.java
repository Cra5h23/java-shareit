package ru.practicum.shareit.booking.dto;

/**
 * Список состояний бронирования.
 *
 * @author Nikolay Radzivon
 * @Date 03.05.2024
 */
public enum BookingState {
    /**
     * Все
     */
    ALL,
    /**
     * Текущие
     */
    CURRENT,
    /**
     * Завершенные
     */
    PAST,
    /**
     * Будущие
     */
    FUTURE,
    /**
     * Ожидающие подтверждения
     */
    WAITING,
    /**
     * Отклонённые
     */
    REJECTED
}
