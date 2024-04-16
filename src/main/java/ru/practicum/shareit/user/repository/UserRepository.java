package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    UserDto save(User user);

    UserDto update(User user, long userId);

    UserDto findById(long userId);

    void deleteById(long userId);

    List<UserDto> findAll();
}