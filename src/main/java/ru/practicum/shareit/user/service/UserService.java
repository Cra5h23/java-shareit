package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.UserSort;

import java.util.List;

/**
 * Интерфейс {@link UserService}
 *
 * @author Nikolay Radzivon
 */
public interface UserService {
    /**
     * Метод добавления нового пользователя.
     *
     * @param user объект класса {@link UserRequestDto}.
     * @return объект класса {@link UserResponseDto}.
     */
    UserResponseDto addNewUser(UserRequestDto user);

    /**
     * Метод для обновления данных пользователей по id.
     *
     * @param user   объект класса {@link UserRequestDto}.
     * @param userId идентификационный номер пользователя.
     * @return обновлённый объект класса {@link UserResponseDto}.
     */
    UserResponseDto updateUser(UserRequestDto user, long userId);

    /**
     * Метод получения пользователя по его id.
     *
     * @param userId идентификационный номер пользователя.
     * @return объект класса {@link UserResponseDto}.
     */
    UserResponseDto getUser(long userId);

    /**
     * Метод удаления пользователя по его id.
     *
     * @param userId идентификационный номер пользователя.
     */
    void deleteUser(long userId);

    /**
     * Метод получения списка всех пользователей.
     *
     * @return {@link List} объектов {@link UserResponseDto}.
     */
    List<UserResponseDto> getAllUsers(int page, int size, UserSort sort);
}
