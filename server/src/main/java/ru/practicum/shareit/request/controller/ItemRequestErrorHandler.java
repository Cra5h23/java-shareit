package ru.practicum.shareit.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.practicum.shareit.exception.NotFoundItemRequestException;
import ru.practicum.shareit.exception.NotFoundUserException;

import javax.validation.ConstraintViolationException;

import static ru.practicum.shareit.exception.ErrorResponse.makeErrorResponse;

/**
 * @author Nikolay Radzivon
 * @Date 19.05.2024
 */
@Slf4j
@ControllerAdvice("ru.practicum.shareit.request")
public class ItemRequestErrorHandler {

    @ExceptionHandler
    public ResponseEntity<?> handlerNotFoundUserException(final NotFoundUserException e, WebRequest webRequest) {
        log.warn("Ошибка работы с запросами", e);
        return makeErrorResponse(webRequest, HttpStatus.NOT_FOUND, "Ошибка работы с запросами: ");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerConstraintViolationException(final ConstraintViolationException e, WebRequest webRequest) {
        log.warn("Ошибка ввода данных запроса", e);
        return makeErrorResponse(webRequest, HttpStatus.BAD_REQUEST, "Ошибка ввода данных запроса: ");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerNotFoundItemRequestException(final NotFoundItemRequestException e, WebRequest webRequest) {
        log.warn("Ошибка работы с запросами", e);
        return makeErrorResponse(webRequest, HttpStatus.NOT_FOUND, "Ошибка работы с запросами: ");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerMissingRequestHeaderException(final MissingRequestHeaderException e,
                                                                  WebRequest webRequest) {
        log.warn("Не указан заголовок", e);
        return makeErrorResponse(webRequest, HttpStatus.BAD_REQUEST, "Не указан заголовок: ");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerMissingServletRequestParameterException(final MissingServletRequestParameterException e,
                                                                            WebRequest webRequest) {
        log.warn("Не указан параметр запроса", e);
        return makeErrorResponse(webRequest, HttpStatus.BAD_REQUEST, "Не указан параметр запроса: ");
    }
}
