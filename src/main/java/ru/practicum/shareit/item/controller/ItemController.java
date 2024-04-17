package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;

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
    public ItemDto addNewItem(@Valid @RequestBody Item item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("POST /items body = {}, header \"X-Sharer-User-Id\" = {}", item, userId);
        return itemService.addNewItem(item, userId);
    }
}
