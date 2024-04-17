package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.shareit.user.exeption.UserRepositoryException;
import ru.practicum.shareit.user.exeption.UserServiceException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс {@link UserErrorHandler} для обработки исключений {@link UserRepositoryException}, {@link UserServiceException}, {@link MethodArgumentNotValidException} в пакете
 *
 * @author Nikolay Radzivon
 * @Date 17.04.2024
 */
@ControllerAdvice("ru.practicum.shareit.user")
@Slf4j
public class UserErrorHandler {

    @ExceptionHandler
    public ResponseEntity<?> handlerUserRepositoryException(final UserRepositoryException e) {
        log.warn("Ошибка работы с пользователями", e);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("timestamp", LocalDateTime.now(),
                        "status", HttpStatus.CONFLICT.value(),
                        "Ошибка работы с пользователями", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerUserServiceException(final UserServiceException e) {
        log.warn("Ошибка работы с пользователями", e);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(Map.of("timestamp", LocalDateTime.now(),
                        "status", HttpStatus.NO_CONTENT.value(),
                        "Ошибка работы с пользователями", e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<?> handlerMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("Ошибка ввода данных пользователя", e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("timestamp", LocalDateTime.now(),
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "Ошибка ввода данных пользователя", e.getFieldErrors().stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.toList())));
    }
}
