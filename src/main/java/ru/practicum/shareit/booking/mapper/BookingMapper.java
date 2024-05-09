package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Класс {@link BookingMapper} для маппинга объекта класса {@link BookingRequestDto} в объект класса {@link Booking},
 * объект класса {@link Booking} в объект класса {@link BookingResponseDto},
 * объект класса {@link Booking} в объект класса {@link BookingShort},
 * {@link Iterable} {@link Booking} в {@link List} {@link BookingResponseDto}.
 *
 * @author Nikolay Radzivon
 * @Date 02.05.2024
 */
public class BookingMapper {
    /**
     * Метод маппинга объекта класса {@link BookingRequestDto} в объект класса {@link Booking}.
     *
     * @param bookingRequestDto объект класса {@link BookingRequestDto} данные бронирования.
     * @param user              объект класса {@link User} пользователь букер.
     * @param item              объект класса {@link Item} бронируемый предмет.
     * @param timeZone          {@link TimeZone} часовой пояс пользователя бронирующего предмет.
     * @return {@link Booking}
     */
    public static Booking toBooking(BookingRequestDto bookingRequestDto, User user, Item item, TimeZone timeZone) {
        return Booking.builder()
                .start(bookingRequestDto.getStart().atZone(timeZone.toZoneId()))
                .end(bookingRequestDto.getEnd().atZone(timeZone.toZoneId()))
                .booker(user)
                .item(item)
                .build();
    }

    /**
     * Метод преобразования объекта класса {@link Booking} в объект класса {@link BookingResponseDto} использующегося в ответе.
     *
     * @param booking  объект класса {@link Booking} данные бронирования.
     * @param timeZone {@link TimeZone} часовой пояс пользователя бронирующего предмет.
     * @return {@link BookingResponseDto}
     */
    public static BookingResponseDto toBookingResponseDto(Booking booking, TimeZone timeZone) {
        String start = booking.getStart().withZoneSameInstant(timeZone.toZoneId()).toLocalDateTime().toString();
        String end = booking.getEnd().withZoneSameInstant(timeZone.toZoneId()).toLocalDateTime().toString();

        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .booker(BookerDto.builder()
                        .id(booking.getBooker().getId())
                        .build())
                .item(ItemBookingDto.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .build())
                .status(booking.getStatus())
                .build();
    }

    /**
     * Метод маппинга объект класса {@link Iterable} {@link Booking} в объект класса {@link List} {@link BookingResponseDto}.
     *
     * @param bookings объект класса {@link Iterable} {@link Booking} данные бронирования.
     * @param timeZone {@link TimeZone} часовой пояс пользователя бронирующего предмет.
     * @return {@link List} {@link BookingResponseDto}
     */
    public static List<BookingResponseDto> toBookingResponseDtoList(Iterable<Booking> bookings, TimeZone timeZone) {
        List<BookingResponseDto> list = new ArrayList<>();
        for (Booking booking : bookings) {
            list.add(toBookingResponseDto(booking, timeZone));
        }
        return list;
    }

    /**
     * Метод маппинга объекта класса {@link Booking} в объект класса {@link BookingShort}.
     *
     * @param booking объект класса {@link Booking} данные бронирования.
     * @return {@link BookingShort }
     */
    public static BookingShort toBookingShort(Booking booking) {
        return BookingShort.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
