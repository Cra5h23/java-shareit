package ru.practicum.shareit.item.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.TimeZone;

/**
 * Контроллер для работы с эндпоинтами /items
 *
 * @author Nikolay Radzivon
 * @Date 17.04.2024
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Взаимодействие с вещами", description = "Контроллер для взаимодействия с вещами")
public class ItemController {
    private final ItemService itemService;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    @Validated(Marker.OnCreate.class)
    @Operation(summary = "Добавление новой вещи", description = "Позволяет добавить новую вещь")
    public ResponseEntity<?> addNewItem(
            @Valid @RequestBody @Parameter(description = "Данные вещи") ItemRequestDto item,
            @RequestHeader(value = xSharerUserId) @Parameter(
                    description = "Идентификационный номер пользователя владельца вещи") Long userId) {
        log.info("POST /items , body = {}, header \"{}\" = {}", item, xSharerUserId, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemService.addNewItem(item, userId));
    }

    @PatchMapping("/{itemId}")
    @Operation(summary = "Обновление вещи", description = "Позволяет обновить вещь (может обновить только владелец вещи)")
    public ResponseEntity<?> updateItem(
            @RequestBody @Parameter(description = "Данные вещи") ItemRequestDto item,
            @PathVariable @Parameter(description = "Идентификационный номер вещи") Long itemId,
            @RequestHeader(value = xSharerUserId) @Parameter(
                    description = "Идентификационный номер пользователя владельца вещи") Long userId) {
        log.info("PATCH /items/{} , body = {}, header \"{}\" = {}", itemId, item, xSharerUserId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.updateItem(item, userId, itemId));
    }

    @GetMapping("/{itemId}")
    @Operation(summary = "Получение вещи по id", description = "Просмотр информации о вещи может запросить любой пользователь")
    public ResponseEntity<?> getItemById(
            @PathVariable @Parameter(description = "Идентификационный номер вещи") Long itemId,
            @RequestHeader(value = xSharerUserId, required = false) @Parameter(
                    description = "Идентификационный номер пользователя") Long userId) {
        log.info("GET /items/{} , header \"{}\" = {}", itemId, xSharerUserId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.getItemByItemId(itemId, userId));
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удаление вещи по id", description = "Удаление вещи (может удалить только пользователь владелец вещи")
    public void deleteItemByItemId(
            @PathVariable @Parameter(description = "Идентификационный номер вещи") Long itemId,
            @RequestHeader(value = xSharerUserId) @Parameter(
                    description = "Идентификационный номер пользователя владельца вещи") Long userId) {
        log.info("DELETE /items/{} , header \"{}\" = {}", itemId, xSharerUserId, userId);

        itemService.deleteItemByItemId(itemId, userId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удаление всех вещей пользователя",
            description = "Удаление всех вещей (Может удалить только пользователь владелец вещей)")
    public void deleteAllItemByUser(
            @RequestHeader(value = xSharerUserId) @Parameter(
                    description = "Идентификационный номер пользователя владельца вещи") Long userId) {
        log.info("DELETE /items , header \"{}\" = {}", xSharerUserId, userId);

        itemService.deleteAllItemByUser(userId);
    }

    @GetMapping
    @Operation(summary = "Получение всех вещей пользователя",
            description = "Получение всех вещей пользователя (может запросить только пользователь владелец вещей)")
    public ResponseEntity<?> getAllItemByUser(
            @RequestHeader(value = xSharerUserId) @Parameter(
                    description = "Идентификационный номер пользователя владельца вещей") Long userId) {
        log.info("GET /items , header \"{}\" = {}", xSharerUserId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.getAllItemByUser(userId));
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск вещей", description = ("Поиск вещей по указанному тексту. " +
            "Поиск происходит по названию или описанию вещи. Выводятся только вещи доступные для аренды"))
    public ResponseEntity<?> searchItemByText(
            @RequestParam @Parameter(description = "Текст для поиска") String text,
            @RequestHeader(value = xSharerUserId) @Parameter(
                    description = "Идентификационный номер пользователя") Long userId) {
        log.info("GET /items/search?text={} , header \"{}\" = {}", text, xSharerUserId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.searchItemByText(text, userId));
    }

    @PostMapping("{itemId}/comment")
    @Validated(Marker.OnCreate.class)
    public ResponseEntity<?> addComment(
            @PathVariable Long itemId,
            @RequestHeader(value = xSharerUserId) Long userId,
            @RequestBody @Valid CommentRequestDto text,
            TimeZone timeZone) {
        log.info("POST /items/{}/comment , header \"{}\" = {}", itemId, xSharerUserId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.addComment(itemId, userId, timeZone, text));
    }

    @PatchMapping("comment/{commentId}")
    public ResponseEntity<?> updateComment(
            @RequestBody CommentRequestDto comment,
            @RequestHeader(value = xSharerUserId) Long userId,
            @PathVariable Long commentId) {
        log.info("PATCH /items/comment/{} , header \"{}\" = {}", commentId, xSharerUserId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.updateComment(comment, userId, commentId));
    }
}
