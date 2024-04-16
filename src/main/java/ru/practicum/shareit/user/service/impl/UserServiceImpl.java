package ru.practicum.shareit.user.service.impl;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {
    /**
     * Метод добавления нового пользователя.
     *
     * @param user объект класса {@link User}.
     * @return объект класса {@link UserDto}.
     */
    @Override
    public UserDto addNewUser(User user) {
        return null;
    }

    /**
     * Метод для обновления данных пользователей по id.
     *
     * @param user   объект класса {@link User}.
     * @param userId идентификационный номер пользователя.
     * @return обновлённый объект класса {@link UserDto}.
     */
    @Override
    public UserDto updateUser(User user, long userId) {
        return null;
    }

    /**
     * Метод получения пользователя по его id.
     *
     * @param userId идентификационный номер пользователя.
     * @return объект класса {@link UserDto}.
     */
    @Override
    public UserDto getUser(long userId) {
        return null;
    }

    /**
     * Метод удаления пользователя по его id.
     *
     * @param userId идентификационный номер пользователя.
     */
    @Override
    public void deleteUser(long userId) {

    }

    /**
     * Метод получения списка всех пользователей.
     *
     * @return {@link List} объектов {@link UserDto}.
     */
    @Override
    public List<UserDto> getAllUsers() {
        return List.of();
    }
}
