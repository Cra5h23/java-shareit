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
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.service.ItemSearchParams;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    @Operation(summary = "Добавление новой вещи", description = "Позволяет добавить новую вещь")
    public ResponseEntity<?> addNewItem(
            @RequestBody @Parameter(description = "Данные вещи") ItemDtoRequest item,
            @RequestHeader(value = xSharerUserId)
            @Parameter(description = "Идентификационный номер пользователя владельца вещи") Long userId) {
        log.info("POST /items , body = {}, header \"{}\" = {}", item, xSharerUserId, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemService.addNewItem(item, userId));
    }

    @PatchMapping("/{itemId}")
    @Operation(summary = "Обновление вещи", description = "Позволяет обновить вещь (может обновить только владелец вещи)")
    public ResponseEntity<?> updateItem(
            @RequestBody @Parameter(description = "Данные вещи") ItemDtoRequest item,
            @PathVariable @Parameter(description = "Идентификационный номер вещи") Long itemId,
            @RequestHeader(value = xSharerUserId)
            @Parameter(description = "Идентификационный номер пользователя владельца вещи") Long userId) {
        log.info("PATCH /items/{} , body = {}, header \"{}\" = {}", itemId, item, xSharerUserId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.updateItem(item, userId, itemId));
    }

    @GetMapping("/{itemId}")
    @Operation(summary = "Получение вещи по id", description = "Просмотр информации о вещи может запросить любой пользователь")
    public ResponseEntity<?> getItemById(
            @PathVariable @Parameter(description = "Идентификационный номер вещи") Long itemId,
            @RequestHeader(value = xSharerUserId, required = false)
            @Parameter(description = "Идентификационный номер пользователя") Long userId) {
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
            @RequestHeader(value = xSharerUserId)
            @Parameter(description = "Идентификационный номер пользователя владельца вещи") Long userId) {
        log.info("DELETE /items/{} , header \"{}\" = {}", itemId, xSharerUserId, userId);

        itemService.deleteItemByItemId(itemId, userId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удаление всех вещей пользователя",
            description = "Удаление всех вещей (Может удалить только пользователь владелец вещей)")
    public void deleteAllItemByUser(
            @RequestHeader(value = xSharerUserId)
            @Parameter(description = "Идентификационный номер пользователя владельца вещи") Long userId) {
        log.info("DELETE /items , header \"{}\" = {}", xSharerUserId, userId);

        itemService.deleteAllItemByUser(userId);
    }

    @GetMapping
    @Operation(summary = "Получение всех вещей пользователя",
            description = "Получение всех вещей пользователя (может запросить только пользователь владелец вещей)")
    public ResponseEntity<?> getAllItemByUser(
            @RequestHeader(value = xSharerUserId)
            @Parameter(description = "Идентификационный номер пользователя владельца вещей")
            Long userId,
            @RequestParam(required = false, name = "from", defaultValue = "0")
            @Min(value = 0, message = "Параметр from не может быть меньше 0.")
            Integer from,
            @RequestParam(required = false, name = "size", defaultValue = "10")
            @Min(value = 1, message = "Параметр size не может быть меньше 0.")
            @Max(value = 100, message = "Параметр size не может быть больше 100.")
            Integer size) {
        log.info("GET /items?from={}&size={} , header \"{}\" = {}", from, size, xSharerUserId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.getAllItemByUser(userId, from, size));
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск вещей", description = ("Поиск вещей по указанному тексту. " +
            "Поиск происходит по названию или описанию вещи. Выводятся только вещи доступные для аренды"))
    public ResponseEntity<?> searchItemByText(
            @RequestParam
            @Parameter(description = "Текст для поиска")
            String text,
            @RequestParam(required = false, name = "from", defaultValue = "0")
            @Min(value = 0, message = "Параметр from не может быть меньше 0.")
            Integer from,
            @RequestParam(required = false, name = "size", defaultValue = "10")
            @Min(value = 1, message = "Параметр size не может быть меньше 0.")
            @Max(value = 100, message = "Параметр size не может быть больше 100.")
            Integer size,
            @RequestHeader(value = xSharerUserId)
            @Parameter(description = "Идентификационный номер пользователя")
            Long userId) {
        log.info("GET /items/search?text={}&from={}&size={} , header \"{}\" = {}", text, from, size, xSharerUserId, userId);

        var params = ItemSearchParams.builder()
                .from(from)
                .size(size)
                .userId(userId)
                .text(text)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(itemService.searchItemByText(params));
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<?> addComment(
            @PathVariable Long itemId,
            @RequestHeader(value = xSharerUserId) Long userId,
            @RequestBody CommentRequestDto text,
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

    @DeleteMapping("comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(
            @PathVariable Long commentId,
            @RequestHeader(value = xSharerUserId) Long userId) {
        log.info("DELETE /items/comment/{} , header \"{}\" = {}", commentId, xSharerUserId, userId);

        itemService.deleteComment(commentId, userId);
    }
}
