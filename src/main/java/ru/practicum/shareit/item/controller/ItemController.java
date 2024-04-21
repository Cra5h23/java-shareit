package ru.practicum.shareit.item.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Взаимодействие с вещами", description = "Контроллер для взаимодействия с вещами")
public class ItemController {
    private final ItemService itemService;
    private final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.OnCreate.class)
    @Operation(summary = "Добавление новой вещи", description = "Позволяет добавить новую вещь")
    public ItemResponseDto addNewItem(@Valid @RequestBody @Parameter(description = "Данные вещи") ItemRequestDto item,
                                      @RequestHeader(value = X_SHARER_USER_ID)
                                      @Parameter(description = "Идентификационный номер пользователя владельца вещи")
                                      Long userId) {
        log.info("POST /items , body = {}, header \"{}\" = {}", item, X_SHARER_USER_ID, userId);
        return itemService.addNewItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновление вещи", description = "Позволяет обновить вещь (может обновить только владелец вещи)")
    public ItemResponseDto updateItem(@RequestBody @Parameter(description = "Данные вещи") ItemRequestDto item,
                                      @PathVariable @Parameter(description = "Идентификационный номер вещи") Long itemId,
                                      @RequestHeader(value = X_SHARER_USER_ID)
                                      @Parameter(description = "Идентификационный номер пользователя владельца вещи")
                                      Long userId) {
        log.info("PATCH /items/{} , body = {}, header \"{}\" = {}", itemId, item, X_SHARER_USER_ID, userId);
        return itemService.updateItem(item, userId, itemId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение вещи по id", description = "Просмотр информации о вещи может запросить любой пользователь")
    public ItemResponseDto getItemById(@PathVariable @Parameter(description = "Идентификационный номер вещи") Long itemId,
                                       @RequestHeader(value = X_SHARER_USER_ID, required = false)
                                       @Parameter(description = "Идентификационный номер пользователя")
                                       Long userId) {
        log.info("GET /items/{} , header \"{}\" = {}", itemId, X_SHARER_USER_ID, userId);
        return itemService.getItemByItemId(itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удаление вещи по id", description = "Удаление вещи (может удалить только пользователь владелец вещи")
    public void deleteItemByItemId(@PathVariable @Parameter(description = "Идентификационный номер вещи") Long itemId,
                                   @RequestHeader(value = X_SHARER_USER_ID)
                                   @Parameter(description = "Идентификационный номер пользователя владельца вещи")
                                   Long userId) {
        log.info("DELETE /items/{} , header \"{}\" = {}", itemId, X_SHARER_USER_ID, userId);
        itemService.deleteItemByItemId(itemId, userId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удаление всех вещей пользователя",
            description = "Удаление всех вещей (Может удалить только пользователь владелец вещей)")
    public void deleteAllItemByUser(@RequestHeader(value = X_SHARER_USER_ID)
                                    @Parameter(description = "Идентификационный номер пользователя владельца вещи")
                                    Long userId) {
        log.info("DELETE /items , header \"{}\" = {}", X_SHARER_USER_ID, userId);
        itemService.deleteAllItemByUser(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение всех вещей пользователя",
            description = "Получение всех вещей пользователя (может запросить только пользователь владелец вещей)")
    public List<ItemResponseDto> getAllItemByUser(@RequestHeader(value = X_SHARER_USER_ID) @Parameter(
            description = "Идентификационный номер пользователя владельца вещей") Long userId) {
        log.info("GET /items , header \"{}\" = {}", X_SHARER_USER_ID, userId);
        return itemService.getAllItemByUser(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск вещей", description = ("Поиск вещей по указанному тексту. " +
            "Поиск происходит по названию или описанию вещи. Выводятся только вещи доступные для аренды"))
    public List<ItemResponseDto> searchItemByText(@RequestParam @Parameter(description = "Текст для поиска") String text,
                                                  @RequestHeader(value = X_SHARER_USER_ID)
                                                  @Parameter(description = "Идентификационный номер пользователя")
                                                  Long userId) {
        log.info("GET /items/search?text={} , header \"{}\" = {}", text, X_SHARER_USER_ID, userId);
        return itemService.searchItemByText(text, userId);
    }
}
