package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.practicum.shareit.user.converter.StringToUserSortConverter;

import javax.validation.ConstraintViolationException;

import static ru.practicum.shareit.exception.ErrorResponse.makeErrorResponse;

/**
 * @author Nikolay Radzivon
 * @Date 31.05.2024
 */
@Slf4j
@ControllerAdvice("ru.practicum.shareit.user")
public class UserErrorHandler {

    @ExceptionHandler
    public ResponseEntity<?> handlerBookingStateException(final StringToUserSortConverter.UserSortException e, WebRequest webRequest) {
        log.warn("Ошибка ввода типа сортировки", e);
        return makeErrorResponse(webRequest, HttpStatus.BAD_REQUEST, "Unknown sort: ");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerConstraintViolationException(final ConstraintViolationException e, WebRequest webRequest) {
        log.warn("Ошибка ввода данных пользователя", e);
        return makeErrorResponse(webRequest, HttpStatus.BAD_REQUEST, "Ошибка ввода данных пользователя: ");
    }
}
