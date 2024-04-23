package ru.practicum.shareit.user.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.exeption.UserRepositoryException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

/**
 * Реализация интерфейса {@link UserRepository} для хранения пользователей в памяти компьютера.
 *
 * @author Nikolay Radzivon.
 */
@Repository
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    private Long generatorId = 0L;
    private final Map<Long, User> userMap = new HashMap<>();

    /**
     * Метод добавления нового пользователя в репозиторий или обновления существующего.
     *
     * @param user объект класса {@link User} для добавления.
     * @return объект класса {@link UserResponseDto}.
     */
    @Override
    public User save(final User user) {
        if (user.getId() != null && userMap.containsKey(user.getId())) {
            checkEmail(user, "обновить");
            userMap.put(user.getId(), user);
        } else {
            checkEmail(user, "добавить");
            var id = ++generatorId;
            user.setId(id);
            userMap.put(id, user);
        }

        return user.toBuilder().build();
    }

    /**
     * Метод получения пользователя по id из репозитория.
     *
     * @param userId идентификационный номер пользователя.
     * @return объект класса {@link User}.
     */
    @Override
    public Optional<User> findById(long userId) {
        log.info("Запрошен пользователь с id {}", userId);
        User user = userMap.get(userId);

        if (user == null) {
            return Optional.empty();
        }

        return Optional.of(user.toBuilder().build());
    }

    /**
     * Метод удаления пользователя по id из репозитория.
     *
     * @param userId идентификационный номер пользователя.
     */
    @Override
    public void deleteById(long userId) {
        log.info("Удалён пользователь с id {}", userId);
        userMap.remove(userId);
    }

    /**
     * Метод получения списка всех пользователей из репозитория.
     *
     * @return {@link Collections} объектов {@link User}.
     */
    @Override
    public Collection<User> findAll() {
        log.info("Запрошен список всех пользователей");
        return Collections.unmodifiableCollection(userMap.values());
    }

    /**
     * Метод проверки, что email не зарегистрирован.
     *
     * @param user    объект класса {@link UserRequestDto}.
     * @param message Сообщение ошибки.
     */
    private void checkEmail(User user, String message) {
        var s = "Нельзя %s нового пользователя, Пользователь с email %s уже существует";

        Optional<User> first = userMap.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()) && !u.getId().equals(user.getId()))
                .findFirst();
        if (first.isPresent()) {
            throw new UserRepositoryException(String.format(s, message, user.getEmail()));
        }
    }
}
