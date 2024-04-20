package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.practicum.shareit.item.exception.ItemRepositoryException;
import static ru.practicum.shareit.ErrorResponse.*;
/**
 * Класс {@link ItemErrorHandler} для обработки исключений {@link ItemRepositoryException}, {@link ItemServiceException},
 * {@link MissingRequestHeaderException}, {@link ConstraintViolationException} в пакете {@link ru.practicum.shareit.item}
 *
 * @author Nikolay Radzivon
 * @Date 19.04.2024
 */
@ControllerAdvice("ru.practicum.shareit.item")
@Slf4j
@RequiredArgsConstructor
public class ItemErrorHandler {
    @ExceptionHandler
    public ResponseEntity<?> handlerItemRepositoryException(final ItemRepositoryException e, WebRequest webRequest) {
        log.warn("Ошибка работы с предметами", e);
        return makeErrorResponse(webRequest, HttpStatus.NOT_FOUND, "Ошибка работы с предметами");
    }

}
