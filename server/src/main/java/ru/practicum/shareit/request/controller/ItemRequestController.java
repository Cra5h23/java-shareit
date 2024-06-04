package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String xSharerUserId = "X-Sharer-User-Id";

    /**
     * Метод создания нового запроса вещи.
     *
     * @param request  {@link ItemRequestDtoRequest} описание вещи которая требуется.
     * @param userId   {@link Long} идентификационный номер пользователя создающего запрос.
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

    @GetMapping
    public ResponseEntity<?> getUserRequests(
            @RequestHeader(value = xSharerUserId) Long userId) {
        log.info("GET /requests , {} = {}", xSharerUserId, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemRequestService.getUserRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllRequests(
            @RequestHeader(value = xSharerUserId) Long userId,
            @RequestParam(required = false, name = "from")
            @Min(value = 0, message = "Параметр from не может быть меньше 0.")
            Integer from,
            @RequestParam(required = false, name = "size")
            @Min(value = 1, message = "Параметр size не может быть меньше 0.")
            @Max(value = 100, message = "Параметр size не может быть больше 100.")
            Integer size) {
        log.info("GET /requests/all?from={}&size={} ,{} = {}", from, size, xSharerUserId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemRequestService.getAllRequests(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<?> getRequestById(
            @RequestHeader(value = xSharerUserId) Long userId,
            @PathVariable Long requestId) {
        log.info("GET /requests/{} ,{} = {}", requestId, xSharerUserId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemRequestService.getRequestById(requestId, userId));
    }
}
