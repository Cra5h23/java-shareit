package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс {@link UserRepository}.
 *
 * @author Nikolay Radzivon.
 */
public interface UserRepository {
    /**
     * Метод добавления нового пользователя в репозиторий.
     *
     * @param user объект класса {@link User} для добавления.
     * @return объект класса {@link UserResponseDto}.
     */
    UserResponseDto save(UserRequestDto user);

    /**
     * Метод обновления пользователя по id в репозитории.
     *
     * @param user   объект класса {@link User} для обновления.
     * @param userId идентификационный номер пользователя.
     * @return объект класса {@link UserResponseDto}.
     */
    UserResponseDto update(UserRequestDto user, long userId);

    /**
     * Метод получения пользователя по id из репозитория.
     *
     * @param userId идентификационный номер пользователя.
     * @return объект класса {@link UserResponseDto}.
     */
    Optional<UserResponseDto> findById(long userId);

    /**
     * Метод удаления пользователя по id из репозитория.
     *
     * @param userId идентификационный номер пользователя.
     */
    void deleteById(long userId);

    /**
     * Метод получения списка всех пользователей из репозитория.
     *
     * @return {@link List} объектов {@link UserResponseDto}.
     */
    List<UserResponseDto> findAll();
}