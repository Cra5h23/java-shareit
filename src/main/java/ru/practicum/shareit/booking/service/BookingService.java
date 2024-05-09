package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;
import java.util.TimeZone;

/**
 * Интерфейс {@link BookingService} логики работы с бронированиями.
 *
 * @author Nikolay Radzivon
 * @Date 02.05.2024
 */
public interface BookingService {

    /**
     * Метод добавления нового бронирования.
     *
     * @param booking  объект класса {@link BookingRequestDto} данные для бронирования.
     * @param userId   объект класса {@link Long} идентификационный номер бронирующего пользователя.
     * @param timeZone объект класса {@link TimeZone} часовой пояс бронирующего пользователя.
     * @return объект класса {@link BookingResponseDto}.
     */
    BookingResponseDto addNewBooking(BookingRequestDto booking, Long userId, TimeZone timeZone);
}
