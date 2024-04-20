package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.practicum.shareit.user.exeption.UserRepositoryException;
import ru.practicum.shareit.user.exeption.UserServiceException;

import javax.validation.ConstraintViolationException;

import static ru.practicum.shareit.ErrorResponse.*;

/**
 * Класс {@link UserErrorHandler} для обработки исключений {@link UserRepositoryException}, {@link UserServiceException},
 * {@link MethodArgumentNotValidException}, {@link ConstraintViolationException} в пакете {@link ru.practicum.shareit.user}
 *
 * @author Nikolay Radzivon
 * @Date 17.04.2024
 */
@ControllerAdvice("ru.practicum.shareit.user")
@Slf4j
public class UserErrorHandler {

    @ExceptionHandler
    public ResponseEntity<?> handlerUserRepositoryException(final UserRepositoryException e, WebRequest webRequest) {
        log.warn("Ошибка работы с пользователями", e);
        return makeErrorResponse(webRequest, HttpStatus.CONFLICT, "Ошибка работы с пользователями");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerUserServiceException(final UserServiceException e, WebRequest webRequest) {
        log.warn("Ошибка работы с пользователями", e);
        return makeErrorResponse(webRequest, HttpStatus.NO_CONTENT, "Ошибка работы с пользователями");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerMethodArgumentNotValidException(final MethodArgumentNotValidException e,
                                                                    WebRequest webRequest) {
        log.warn("Ошибка ввода данных пользователя", e);
        return makeErrorResponse(webRequest, HttpStatus.BAD_REQUEST, "Ошибка ввода данных пользователя");
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerConstraintViolationException(final ConstraintViolationException e, WebRequest webRequest) {
        log.warn("Ошибка ввода данных пользователя", e);
        return makeErrorResponse(webRequest, HttpStatus.BAD_REQUEST, "Ошибка ввода данных пользователя");
    }
}
