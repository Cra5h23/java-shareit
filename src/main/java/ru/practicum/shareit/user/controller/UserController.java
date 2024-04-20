package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
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
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.OnCreate.class)
    public UserDto addNewUser(@RequestBody @Valid User user) {
        log.info("POST /users body= {}", user);
        return userService.addNewUser(user);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Validated({Marker.OnUpdate.class})
    public UserDto updateUser(@RequestBody @Valid User user, @PathVariable long userId) {
        log.info("PATCH /users/{} body= {}", userId, user);
        return userService.updateUser(user, userId);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUser(@PathVariable(required = false) long userId) {
        log.info("GET /users/{}", userId);
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable long userId) {
        log.info("DELETE /users/{}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers() {
        log.info("GET /users");
        return userService.getAllUsers();
    }
}
