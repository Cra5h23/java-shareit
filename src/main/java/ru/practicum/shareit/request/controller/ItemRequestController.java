package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.TimeZone;

/**
 * Контроллер для эндпоинтов /requests
 *
 * @author Nikolay Radzivon
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String xSharerUserId = "X-Sharer-User-Id";

    /**
     * Метод создания нового запроса вещи.
     * @param request {@link ItemRequestDtoRequest} описание вещи которая требуется.
     * @param userId {@link Long} идентификационный номер пользователя создающего запрос.
     * @param timeZone {@link TimeZone} часовой пояс пользователя.
     * @return {@link ResponseEntity}
     */
    @PostMapping
    public ResponseEntity<?> addNewRequest(
            @RequestBody ItemRequestDtoRequest request,
            @RequestHeader(value = xSharerUserId) Long userId,
            TimeZone timeZone) {
        log.info("POST /requests , body = {} , {} = {}", request, xSharerUserId, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemRequestService.addNewRequest(request, userId, timeZone));
    }
}
