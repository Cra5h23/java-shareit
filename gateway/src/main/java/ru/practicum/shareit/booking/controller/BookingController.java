package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.TimeZone;

/**
 * @author Nikolay Radzivon
 * @Date 31.05.2024
 */
@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ResponseEntity<?> addNewBooking(
            @Valid @RequestBody BookingRequestDto booking,
            @RequestHeader(value = xSharerUserId) Long userId) {
        log.info("POST /bookings, body = {}, header \"{}\" = {}", booking, xSharerUserId, userId);

        return bookingClient.bookItem(userId, booking);
    }

    /**
     * Метод для эндпоинта PATCH /bookings/{bookingId} подтверждение бронирования пользователем владельцем вещи.
     *
     * @param bookingId {@link Long} идентификационный номер блокирования.
     * @param approved  {@link Boolean} подтверждение бронирования.
     * @param userId    {@link Long} идентификационный номер пользователя владельца вещи.
     * @return {@link ResponseEntity}
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<?> bookingConfirmation(
            @PathVariable Long bookingId,
            @RequestParam @NotNull Boolean approved,
            @RequestHeader(value = xSharerUserId) Long userId) {
        log.info("PATCH /bookings/{}?approved={} , header \"{}\" = {}", bookingId, approved, xSharerUserId, userId);

        return bookingClient.confirmationBooking(userId, approved, bookingId);
    }

    /**
     * Метод для эндпоинта GET /bookings/{bookingId} получение бронирования для пользователя.
     *
     * @param bookingId {@link Long} идентификационный номер блокирования.
     * @param userId    {@link Long} идентификационный номер пользователя букера вещи.
     * @return {@link ResponseEntity}
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBooking(
            @PathVariable Long bookingId,
            @RequestHeader(value = xSharerUserId) Long userId) {
        log.info("GET /bookings/{} , header \"{}\" = {}", bookingId, xSharerUserId, userId);

        return bookingClient.getBooking(userId, bookingId);
    }

    /**
     * Метод для эндпоинта GET /booking получение бронирования для пользователя букера.
     *
     * @param state  {@link BookingState} параметр сортировки.
     * @param userId {@link Long} идентификационный номер пользователя букера вещей.
     * @param size
     * @return {@link ResponseEntity}
     */
    @GetMapping
    public ResponseEntity<?> getBookingByUser(
            @RequestParam(required = false, name = "state", defaultValue = "ALL") BookingState state,
            @RequestHeader(value = xSharerUserId) Long userId,
            @RequestParam(required = false, name = "from", defaultValue = "0")
            @Min(value = 0, message = "Параметр from не может быть меньше 0.")
            Integer from,
            @RequestParam(required = false, name = "size", defaultValue = "10")
            @Min(value = 1, message = "Параметр size не может быть меньше 0.")
            @Max(value = 100, message = "Параметр size не может быть больше 100.")
            Integer size) {
        log.info("GET /bookings?state={}&from={}&size={} , header \"{}\" = {}", state, from, size, xSharerUserId, userId);

        return bookingClient.getBookings(userId, state, from, size);
    }

    /**
     * Метод для эндпоинта GET /booking/owner получение бронирования для пользователя букера.
     *
     * @param state  {@link BookingState} параметр сортировки.
     * @param userId {@link Long} идентификационный номер пользователя владельца вещей.
     * @param size   {@link TimeZone} часовой пояс пользователя.
     * @return {@link ResponseEntity}
     */
    @GetMapping("/owner")
    public ResponseEntity<?> getBookingByOwner(
            @RequestParam(required = false, name = "state", defaultValue = "ALL") BookingState state,
            @RequestHeader(value = xSharerUserId) Long userId,
            @RequestParam(required = false, name = "from", defaultValue = "0")
            @Min(value = 0, message = "Параметр from не может быть меньше 0.")
            Integer from,
            @RequestParam(required = false, name = "size", defaultValue = "10")
            @Min(value = 1, message = "Параметр size не может быть меньше 0.")
            @Max(value = 100, message = "Параметр size не может быть больше 100.")
            Integer size) {
        log.info("GET /bookings/owner?state={}&from={}&size={} , header \"{}\" = {}", state, from, size, xSharerUserId, userId);

        return bookingClient.getBookingsOwner(userId, state, from, size);
    }
}
