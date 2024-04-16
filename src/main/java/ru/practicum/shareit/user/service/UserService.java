package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * Интерфейс {@link UserService}
 *
 * @author Nikolay Radzivon
 */
public interface UserService {
    /**
     * Метод добавления нового пользователя.
     * @param user объект класса {@link User}.
     * @return объект класса {@link UserDto}.
     */
    UserDto addNewUser(User user);

    /**
     * Метод для обновления данных пользователей по id.
     * @param user объект класса {@link User}.
     * @param userId идентификационный номер пользователя.
     * @return обновлённый объект класса {@link UserDto}.
     */
    UserDto updateUser(User user, long userId);

    /**
     * Метод получения пользователя по его id.
     * @param userId идентификационный номер пользователя.
     * @return объект класса {@link UserDto}.
     */
    UserDto getUser(long userId);

    /**
     * Метод удаления пользователя по его id.
     * @param userId идентификационный номер пользователя.
     */
    void deleteUser(long userId);

    /**
     * Метод получения списка всех пользователей.
     * @return {@link List} объектов {@link UserDto}.
     */
    List<UserDto> getAllUsers();
}
