package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.practicum.shareit.booking.converter.StringToBookingStateConverter;

import javax.validation.ConstraintViolationException;

import static ru.practicum.shareit.exception.ErrorResponse.makeErrorResponse;

/**
 * @author Nikolay Radzivon
 * @Date 31.05.2024
 */
@ControllerAdvice("ru.practicum.shareit.booking")
@Slf4j
public class BookingErrorHandler {
    @ExceptionHandler
    public ResponseEntity<?> handlerBookingStateException(final StringToBookingStateConverter.BookingStateException e, WebRequest webRequest) {
        log.warn("Ошибка ввода данных бронирования", e);
        return makeErrorResponse(webRequest, HttpStatus.BAD_REQUEST, "Unknown state: ");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerConstraintViolationException(final ConstraintViolationException e, WebRequest webRequest) {
        log.warn("Ошибка ввода данных бронирования", e);
        return makeErrorResponse(webRequest, HttpStatus.BAD_REQUEST, "Ошибка ввода данных бронирования: ");
    }
}
