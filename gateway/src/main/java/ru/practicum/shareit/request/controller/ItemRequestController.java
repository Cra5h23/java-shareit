package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author Nikolay Radzivon
 * @Date 31.05.2024
 */
@Slf4j
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ResponseEntity<?> addNewRequest(
            @Valid @RequestBody ItemRequestDtoRequest request,
            @RequestHeader(value = xSharerUserId) Long userId) {
        log.info("POST /requests , body = {} , {} = {}", request, xSharerUserId, userId);

        return itemRequestClient.addItemRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<?> getUserRequests(
            @RequestHeader(value = xSharerUserId) Long userId) {
        log.info("GET /requests , {} = {}", xSharerUserId, userId);

        return itemRequestClient.getUserRequests(userId);
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

        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<?> getRequestById(
            @RequestHeader(value = xSharerUserId) Long userId,
            @PathVariable Long requestId) {
        log.info("GET /requests/{} ,{} = {}", requestId, xSharerUserId, userId);

        return itemRequestClient.getRequest(requestId, userId);
    }
}
