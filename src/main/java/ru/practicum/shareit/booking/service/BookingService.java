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

    /**
     * Метод подтверждения или отклонения бронирования, пользователем владельцем вещи.
     *
     * @param bookingId объект класса {@link Long} идентификационный номер бронирования.
     * @param userId    объект класса {@link Long} идентификационный номер пользователя.
     * @param approved  объект класса {@link Boolean} подтверждение бронирования.
     * @param timeZone  объект класса {@link TimeZone} часовой пояс пользователя владельца вещи.
     * @return объект класса {@link BookingResponseDto}.
     */
    BookingResponseDto bookingConfirmation(Long bookingId, Long userId, Boolean approved, TimeZone timeZone);

    /**
     * Метод получения бронирования для указанного пользователя.
     *
     * @param bookingId объект класса {@link Long} идентификационный номер бронирования.
     * @param userId    {@link Long} идентификационный номер пользователя.
     * @param timeZone  объект класса {@link TimeZone} часовой пояс бронирующего пользователя.
     * @return объект класса {@link BookingResponseDto}.
     */
    BookingResponseDto getBooking(Long bookingId, Long userId, TimeZone timeZone);

    /**
     * Метод получения списка бронирований для бронирующего.
     *
     * @param userId   объект класса  {@link Long} идентификационный номер пользователя.
     * @param state    объект класса {@link BookingState} состояние бронирования по которому запросить список.
     * @param timeZone объект класса {@link TimeZone} часовой пояс пользователя бронирующего вещь.
     * @return {@link List} объектов класса {@link BookingResponseDto}.
     */
    List<BookingResponseDto> getBookingsByBooker(Long userId, BookingState state, TimeZone timeZone);

    /**
     * Метод получения списка бронирований для владельца вещей.
     *
     * @param userId   объект класса  {@link Long} идентификационный номер пользователя владельца вещей.
     * @param state    объект класса {@link BookingState} состояние бронирования по которому запросить список.
     * @param timeZone объект класса {@link TimeZone} часовой пояс пользователя владельца вещей.
     * @return {@link List} объектов класса {@link BookingResponseDto}.
     */
    List<BookingResponseDto> getBookingByOwner(Long userId, BookingState state, TimeZone timeZone);
}
