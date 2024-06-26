package ru.practicum.shareit.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.UserSort;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Контроллер для работы с эндпоинтами /users
 *
 * @author Nikolay Radzivon
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Взаимодействие с пользователями", description = "Контроллер для взаимодействия с пользователями")
public class UserController {
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Добавление нового пользователя", description = "Позволяет добавить нового пользователя")
    public ResponseEntity<?> addNewUser(
            @RequestBody @Parameter(description = "Данные пользователя")
            UserRequestDto user) {
        log.info("POST /users body = {}", user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.addNewUser(user));
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "Обновление пользователя", description = "Позволяет обновить пользователя")
    public ResponseEntity<?> updateUser(
            @RequestBody @Parameter(description = "Данные пользователя")
            UserRequestDto user,
            @PathVariable @Parameter(description = "Идентификатор пользователя для обновления") long userId) {
        log.info("PATCH /users/{} body = {}", userId, user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateUser(user, userId));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Получение пользователя по id", description = "Позволяет получить пользователя по его id")
    public ResponseEntity<?> getUser(
            @PathVariable(required = false) @Parameter(description = "Идентификатор пользователя") long userId) {
        log.info("GET /users/{}", userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUser(userId));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удаление пользователя по id", description = "Позволяет удалить пользователя по его id")
    public void deleteUser(@PathVariable @Parameter(description = "Идентификатор пользователя") long userId) {
        log.info("DELETE /users/{}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping
    @Operation(summary = "Получение списка всех пользователей", description = "Позволяет запросить список всех пользователей")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false, name = "page", defaultValue = "1") @Min(1) int page,
            @RequestParam(required = false, name = "size", defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(required = false, name = "sort", defaultValue = "NONE") UserSort sort) {
        log.info("GET /users");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getAllUsers(page, size, sort));
    }
}
