package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс {@link UserRepository}.
 *
 * @author Nikolay Radzivon.
 */
public interface UserRepository {
    /**
     * Метод добавления нового пользователя в репозиторий или обновления существующего.
     *
     * @param user объект класса {@link User} для добавления.
     * @return объект класса {@link UserResponseDto}.
     */
    User save(User user);

    /**
     * Метод получения пользователя по id из репозитория.
     *
     * @param userId идентификационный номер пользователя.
     * @return объект класса {@link UserResponseDto}.
     */
    Optional<User> findById(long userId);

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
    Collection<User> findAll();
}