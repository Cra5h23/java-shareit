package ru.practicum.shareit.booking.model;

/**
 * Статус бронирования.
 *
 * @author Nikolay Radzivon
 * @Date 02.05.2024
 */
public enum BookingStatus {
    /**
     * Бронирование ожидает одобрение
     */
    WAITING,
    /**
     * Бронирование подтверждено владельцем
     */
    APPROVED,
    /**
     * Бронирование отклонено владельцем
     */
    REJECTED,
    /**
     * Бронирование отменено создателем
     */
    CANCELED
}
