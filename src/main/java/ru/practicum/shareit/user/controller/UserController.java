package ru.practicum.shareit.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

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
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.OnCreate.class)
    @Operation(summary = "Добавление нового пользователя", description = "Позволяет добавить нового пользователя")
    public UserResponseDto addNewUser(@RequestBody @Parameter(description = "Данные пользователя") @Valid
                                      UserRequestDto user) {
        log.info("POST /users body= {}", user);
        return userService.addNewUser(user);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Validated({Marker.OnUpdate.class})
    @Operation(summary = "Обновление пользователя", description = "Позволяет обновить пользователя")
    public UserResponseDto updateUser(@RequestBody @Parameter(description = "Данные пользователя") @Valid
                                      UserRequestDto user,
                                      @PathVariable @Parameter(description = "Идентификатор пользователя для обновления")
                                      long userId) {
        log.info("PATCH /users/{} body= {}", userId, user);
        return userService.updateUser(user, userId);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение пользователя по id", description = "Позволяет получить пользователя по его id")
    public UserResponseDto getUser(@PathVariable(required = false) @Parameter(description = "Идентификатор пользователя")
                                   long userId) {
        log.info("GET /users/{}", userId);
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удаление пользователя по id", description = "Позволяет удалить пользователя по его id")
    public void deleteUser(@PathVariable @Parameter(description = "Идентификатор пользователя") long userId) {
        log.info("DELETE /users/{}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение списка всех пользователей", description = "Позволяет запросить список всех пользователей")
    public List<UserResponseDto> getAllUsers() {
        log.info("GET /users");
        return userService.getAllUsers();
    }
}
