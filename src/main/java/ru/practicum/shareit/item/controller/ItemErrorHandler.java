package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.practicum.shareit.exception.NotFoundBookingException;
import ru.practicum.shareit.exception.NotFoundCommentException;
import ru.practicum.shareit.exception.NotFoundItemException;
import ru.practicum.shareit.exception.NotFoundUserException;

import javax.validation.ConstraintViolationException;

import static ru.practicum.shareit.exception.ErrorResponse.makeErrorResponse;

/**
 * Класс {@link ItemErrorHandler} для обработки исключений {@link NotFoundItemException}, {@link NotFoundUserException},
 * {@link MissingRequestHeaderException}, {@link ConstraintViolationException} в пакете {@link ru.practicum.shareit.item}
 *
 * @author Nikolay Radzivon
 * @Date 19.04.2024
 */
@ControllerAdvice("ru.practicum.shareit.item")
@Slf4j
public class ItemErrorHandler {
    @ExceptionHandler
    public ResponseEntity<?> handlerConstraintViolationException(final ConstraintViolationException e,
                                                                 WebRequest webRequest) {
        log.warn("Ошибка ввода данных предмета", e);
        return makeErrorResponse(webRequest, HttpStatus.BAD_REQUEST, "Ошибка ввода данных предмета: ");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerMissingRequestHeaderException(final MissingRequestHeaderException e,
                                                                  WebRequest webRequest) {
        log.warn("Не указан пользователь владелец предмета", e);
        return makeErrorResponse(webRequest, HttpStatus.BAD_REQUEST, "Не указан пользователь владелец предмета: ");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerBookingNotFoundException(final NotFoundBookingException e, WebRequest webRequest) {
        log.warn("Ошибка работы с бронированиями", e);
        return makeErrorResponse(webRequest, HttpStatus.BAD_REQUEST, "Ошибка работы с бронированиями: ");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerNotFoundCommentException(final NotFoundCommentException e, WebRequest webRequest) {
        log.warn("Ошибка работы с комментариями", e);
        return makeErrorResponse(webRequest, HttpStatus.BAD_REQUEST, "Ошибка работы с комментариями: ");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerNotFoundItemException(final NotFoundItemException e, WebRequest webRequest) {
        log.warn("Ошибка работы с предметами", e);
        return makeErrorResponse(webRequest, HttpStatus.NOT_FOUND, "Ошибка работы с предметами: ");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerNotFoundUserException(final NotFoundUserException e, WebRequest webRequest) {
        log.warn("Ошибка работы с предметами", e);
        return makeErrorResponse(webRequest, HttpStatus.NOT_FOUND, "Ошибка работы с предметами: ");
    }
}
