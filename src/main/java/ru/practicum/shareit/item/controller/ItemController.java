package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
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
public class ItemController {
    private final ItemService itemService;
    private final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addNewItem(@Valid @RequestBody Item item, @RequestHeader(value = X_SHARER_USER_ID) Long userId) {
        log.info("POST /items , body = {}, header \"{}\" = {}", item, X_SHARER_USER_ID, userId);
        return itemService.addNewItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestBody Item item,
                              @PathVariable Long itemId,
                              @RequestHeader(value = X_SHARER_USER_ID) Long userId) {
        log.info("PATCH /items/{} , body = {}, header \"{}\" = {}", itemId, item, X_SHARER_USER_ID, userId);
        return itemService.updateItem(item, userId, itemId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItemById(@PathVariable Long itemId, @RequestHeader(value = X_SHARER_USER_ID) Long userId) {
        log.info("GET /items/{} , header \"{}\" = {}", itemId, X_SHARER_USER_ID, userId);
        return itemService.getItemByItemId(itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteItemByItemId(@PathVariable Long itemId, @RequestHeader(value = X_SHARER_USER_ID) Long userId) {
        log.info("DELETE /items/{} , header \"{}\" = {}", itemId, X_SHARER_USER_ID, userId);
        itemService.deleteItemByItemId(itemId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAllItemByUser(@RequestHeader(value = X_SHARER_USER_ID) Long userId) {
        log.info("GET /items , header \"{}\" = {}", X_SHARER_USER_ID, userId);
        return itemService.getAllItemByUser(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchItemByText(@RequestParam String text, @RequestHeader(value = X_SHARER_USER_ID) Long userId) {
        log.info("GET /items/search?text={} , header \"{}\" = {}",text,  X_SHARER_USER_ID, userId);
        return itemService.searchItemByText(text, userId);
    }
}
