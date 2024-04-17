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
}
