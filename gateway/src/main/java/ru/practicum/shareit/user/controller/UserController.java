package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserSort;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author Nikolay Radzivon
 * @Date 31.05.2024
 */
@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ResponseEntity<?> addNewUser(@RequestBody @Valid UserRequestDto user) {
        log.info("POST /users body = {}", user);

        return userClient.addUser(user);
    }

    @PatchMapping("/{userId}")
    @Validated({Marker.OnUpdate.class})
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserRequestDto user, @PathVariable long userId) {
        log.info("PATCH /users/{} body = {}", userId, user);

        return userClient.updateUser(userId, user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable(required = false) long userId) {
        log.info("GET /users/{}", userId);

        return userClient.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable long userId) {
        log.info("DELETE /users/{}", userId);

        return userClient.deleteUser(userId);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false, name = "page", defaultValue = "1") @Min(1) int page,
            @RequestParam(required = false, name = "size", defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(required = false, name = "sort", defaultValue = "NONE") UserSort sort) {
        log.info("GET /users");

        return userClient.getUsers(page, size, sort);
    }
}
