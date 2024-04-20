package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

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
public class ItemController {
    private final ItemService itemService;
    private final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.OnCreate.class)
    public ItemResponseDto addNewItem(@Valid @RequestBody ItemRequestDto item,
                                      @RequestHeader(value = X_SHARER_USER_ID) Long userId) {
        log.info("POST /items , body = {}, header \"{}\" = {}", item, X_SHARER_USER_ID, userId);
        return itemService.addNewItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseDto updateItem(@RequestBody ItemRequestDto item,
                                      @PathVariable Long itemId,
                                      @RequestHeader(value = X_SHARER_USER_ID) Long userId) {
        log.info("PATCH /items/{} , body = {}, header \"{}\" = {}", itemId, item, X_SHARER_USER_ID, userId);
        return itemService.updateItem(item, userId, itemId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponseDto getItemById(@PathVariable Long itemId,
                                       @RequestHeader(value = X_SHARER_USER_ID) Long userId) {
        log.info("GET /items/{} , header \"{}\" = {}", itemId, X_SHARER_USER_ID, userId);
        return itemService.getItemByItemId(itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteItemByItemId(@PathVariable Long itemId, @RequestHeader(value = X_SHARER_USER_ID) Long userId) {
        log.info("DELETE /items/{} , header \"{}\" = {}", itemId, X_SHARER_USER_ID, userId);
        itemService.deleteItemByItemId(itemId, userId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllItemByUser(@RequestHeader(value = X_SHARER_USER_ID) Long userId) {
        log.info("DELETE /items , header \"{}\" = {}", X_SHARER_USER_ID, userId);
        itemService.deleteAllItemByUser(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponseDto> getAllItemByUser(@RequestHeader(value = X_SHARER_USER_ID) Long userId) {
        log.info("GET /items , header \"{}\" = {}", X_SHARER_USER_ID, userId);
        return itemService.getAllItemByUser(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponseDto> searchItemByText(@RequestParam String text,
                                                  @RequestHeader(value = X_SHARER_USER_ID) Long userId) {
        log.info("GET /items/search?text={} , header \"{}\" = {}", text, X_SHARER_USER_ID, userId);
        return itemService.searchItemByText(text, userId);
    }
}
