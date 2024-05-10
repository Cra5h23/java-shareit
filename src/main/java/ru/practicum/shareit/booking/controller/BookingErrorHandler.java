package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.practicum.shareit.booking.exception.BookingServiceException;
import ru.practicum.shareit.exception.*;

import javax.validation.ConstraintViolationException;

import static ru.practicum.shareit.exception.ErrorResponse.makeErrorResponse;

/**
 * Класс {@link BookingErrorHandler} для обработки исключений {@link BookingServiceException}, {@link NotFoundUserException},
 * {@link NotFoundItemException}, {@link BookingStateException}, {@link ConstraintViolationException}, {@link NotFoundBookingException}
 * в пакете ru.practicum.shareit.booking
 *
 * @author Nikolay Radzivon
 * @Date 03.05.2024
 */
@ControllerAdvice("ru.practicum.shareit.booking")
@Slf4j
public class BookingErrorHandler {
    @ExceptionHandler
    public ResponseEntity<?> handlerBookingServiceException(final BookingServiceException e, WebRequest webRequest) {
        log.info("Ошибка работы с бронированиями", e);
        return ErrorResponse.makeErrorResponse(webRequest, HttpStatus.BAD_REQUEST, "Ошибка работы с бронированиями: ");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerNotFoundUserException(final NotFoundUserException e, WebRequest webRequest) {
        log.info("Ошибка работы с бронированиями", e);
        return ErrorResponse.makeErrorResponse(webRequest, HttpStatus.NOT_FOUND, "Ошибка работы с бронированиями: ");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerNotFoundItemException(final NotFoundItemException e, WebRequest webRequest) {
        log.info("Ошибка работы с бронированиями", e);
        return ErrorResponse.makeErrorResponse(webRequest, HttpStatus.NOT_FOUND, "Ошибка работы с бронированиями: ");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerConstraintViolationException(final ConstraintViolationException e, WebRequest webRequest) {
        log.warn("Ошибка ввода данных бронирования", e);
        return makeErrorResponse(webRequest, HttpStatus.BAD_REQUEST, "Ошибка ввода данных бронирования: ");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerBookingStateException(final BookingStateException e, WebRequest webRequest) {
        log.warn("Ошибка ввода данных бронирования", e);
        return makeErrorResponse(webRequest, HttpStatus.BAD_REQUEST, "Unknown state: ");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerNotFoundBookingException(final NotFoundBookingException e, WebRequest webRequest) {
        log.warn("Ошибка ввода данных бронирования", e);
        return makeErrorResponse(webRequest, HttpStatus.NOT_FOUND, "Ошибка работы с бронированиями: ");
    }
}
