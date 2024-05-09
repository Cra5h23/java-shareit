package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.TimeZone;

/**
 * Контроллер для работы с эндпоинтами /bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;
    private final String xSharerUserId = "X-Sharer-User-Id";

    /**
     * Метод для эндпоинта POST /bookings создание нового бронирования.
     *
     * @param booking  {@link BookingRequestDto} данные бронирования.
     * @param userId   {@link Long} идентификационный номер пользователя букера.
     * @param timeZone {@link TimeZone} часовой пояс пользователя.
     * @return {@link ResponseEntity}
     */
    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ResponseEntity<?> addNewBooking(
            @Valid @RequestBody BookingRequestDto booking,
            @RequestHeader(value = xSharerUserId) Long userId,
            TimeZone timeZone) {
        log.info("POST /bookings, body = {}, header \"{}\" = {}", booking, xSharerUserId, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingService.addNewBooking(booking, userId, timeZone));
    }

    /**
     * Метод для эндпоинта PATCH /bookings/{bookingId} подтверждение бронирования пользователем владельцем вещи.
     *
     * @param bookingId {@link Long} идентификационный номер блокирования.
     * @param approved  {@link Boolean} подтверждение бронирования.
     * @param userId    {@link Long} идентификационный номер пользователя владельца вещи.
     * @param timeZone  {@link TimeZone} часовой пояс пользователя.
     * @return {@link ResponseEntity}
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<?> bookingConfirmation(
            @PathVariable Long bookingId,
            @RequestParam @NotNull Boolean approved,
            @RequestHeader(value = xSharerUserId) Long userId,
            TimeZone timeZone) {
        log.info("PATCH /bookings/{}?approved={} , header \"{}\" = {}", bookingId, approved, xSharerUserId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingService.bookingConfirmation(bookingId, userId, approved, timeZone));
    }

    }
}
