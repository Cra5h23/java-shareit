package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import static ru.practicum.shareit.exception.ErrorResponse.makeErrorResponse;

/**
 * @author Nikolay Radzivon
 * @Date 02.06.2024
 */
@ControllerAdvice("ru.practicum.shareit")
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<?> handlerMissingRequestHeaderException(final MissingRequestHeaderException e, WebRequest webRequest) {
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
