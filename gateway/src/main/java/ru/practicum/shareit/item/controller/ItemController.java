package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.TimeZone;

/**
 * @author Nikolay Radzivon
 * @Date 31.05.2024
 */
@Slf4j
@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ResponseEntity<?> addNewItem(
            @Valid @RequestBody ItemDtoRequest item,
            @RequestHeader(value = xSharerUserId) Long userId) {
        log.info("POST /items , body = {}, header \"{}\" = {}", item, xSharerUserId, userId);

        return itemClient.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<?> updateItem(
            @RequestBody ItemDtoRequest item,
            @PathVariable Long itemId,
            @RequestHeader(value = xSharerUserId) Long userId) {
        log.info("PATCH /items/{} , body = {}, header \"{}\" = {}", itemId, item, xSharerUserId, userId);

        return itemClient.updateItem(itemId, userId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<?> getItemById(
            @PathVariable Long itemId,
            @RequestHeader(value = xSharerUserId) Long userId, TimeZone timeZone) {
        log.info("GET /items/{} , header \"{}\" = {}", itemId, xSharerUserId, userId);

        return itemClient.getItem(itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItemByItemId(
            @PathVariable Long itemId,
            @RequestHeader(value = xSharerUserId) Long userId) {
        log.info("DELETE /items/{} , header \"{}\" = {}", itemId, xSharerUserId, userId);

        return itemClient.deleteItem(itemId, userId);

    }

    @DeleteMapping
    public ResponseEntity<Object> deleteAllItemByUser(
            @RequestHeader(value = xSharerUserId) Long userId) {
        log.info("DELETE /items , header \"{}\" = {}", xSharerUserId, userId);
        return itemClient.deleteAllUserItems(userId);
    }

    @GetMapping
    public ResponseEntity<?> getAllItemByUser(
            @RequestHeader(value = xSharerUserId) Long userId,
            @RequestParam(required = false, name = "from", defaultValue = "0")
            @Min(value = 0, message = "Параметр from не может быть меньше 0.")
            Integer from,
            @RequestParam(required = false, name = "size", defaultValue = "10")
            @Min(value = 1, message = "Параметр size не может быть меньше 0.")
            @Max(value = 100, message = "Параметр size не может быть больше 100.")
            Integer size) {
        log.info("GET /items?from={}&size={} , header \"{}\" = {}", from, size, xSharerUserId, userId);

        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchItemByText(
            @RequestParam String text,
            @RequestParam(required = false, name = "from", defaultValue = "0")
            @Min(value = 0, message = "Параметр from не может быть меньше 0.")
            Integer from,
            @RequestParam(required = false, name = "size", defaultValue = "10")
            @Min(value = 1, message = "Параметр size не может быть меньше 0.")
            @Max(value = 100, message = "Параметр size не может быть больше 100.")
            Integer size,
            @RequestHeader(value = xSharerUserId) Long userId) {
        log.info("GET /items/search?text={}&from={}&size={} , header \"{}\" = {}", text, from, size, xSharerUserId, userId);

        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("{itemId}/comment")
    @Validated(Marker.OnCreate.class)
    public ResponseEntity<?> addComment(
            @PathVariable Long itemId,
            @RequestHeader(value = xSharerUserId) Long userId,
            @RequestBody @Valid CommentRequestDto text) {
        log.info("POST /items/{}/comment , header \"{}\" = {}", itemId, xSharerUserId, userId);

        return itemClient.addComment(itemId, userId, text);
    }

    @PatchMapping("comment/{commentId}")
    public ResponseEntity<?> updateComment(
            @RequestBody @Valid CommentRequestDto comment,
            @RequestHeader(value = xSharerUserId) Long userId,
            @PathVariable Long commentId) {
        log.info("PATCH /items/comment/{} , header \"{}\" = {}", commentId, xSharerUserId, userId);

        return itemClient.updateComment(commentId, userId, comment);
    }

    @DeleteMapping("comment/{commentId}")
    public ResponseEntity<Object> deleteComment(
            @PathVariable Long commentId,
            @RequestHeader(value = xSharerUserId) Long userId) {
        log.info("DELETE /items/comment/{} , header \"{}\" = {}", commentId, xSharerUserId, userId);

        return itemClient.deleteComment(commentId, userId);
    }
}
