package ru.practicum.shareit.user.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса {@link UserRepository} для хранения пользователей в памяти компьютера.
 *
 * @author Nikolay Radzivon.
 */
@Repository
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> userMap = new HashMap<>();
    private final UserMapper userMapper = new UserMapper();
    private Long generatorId = 0L;

    /**
     * Метод добавления нового пользователя в репозиторий.
     *
     * @param user объект класса {@link User} для добавления.
     * @return объект класса {@link UserDto}.
     */
    @Override
    public UserDto save(User user) {
        checkEmail(user, String.format(
                "Нельзя добавить нового пользователя, Пользователь с email %s уже существует", user.getEmail()));
        var id = ++generatorId;

        userMap.put(id, user);
        log.info("В репозиторий добавлен новый пользователь {} и присвоен id {}", user, id);
        return userMapper.toUserDto(user, id);
    }

    /**
     * Метод обновления пользователя по id в репозитории.
     *
     * @param user   объект класса {@link User} для обновления.
     * @param userId идентификационный номер пользователя.
     * @return объект класса {@link UserDto}.
     */
    @Override
    public UserDto update(User user, long userId) {
        var updateUser = userMap.get(userId);
        if (updateUser == null) {
            throw new UserRepositoryException(String.format("Не существует пользователя с id %d", userId));
        }

        log.info("Обновлён пользователь с id {} , старые данные {} новые данные {}", userId, updateUser, user);

        if (user.getEmail() != null) {
            if (!updateUser.getEmail().equals(user.getEmail())) {
                checkEmail(user, String.format(
                        "Нельзя обновить пользователя с id %d, пользователь с email %s уже существует", userId, user.getEmail()));
                updateUser.setEmail(user.getEmail());
            }
        }

        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }


        return userMapper.toUserDto(updateUser, userId);
    }

    /**
     * Метод получения пользователя по id из репозитория.
     *
     * @param userId идентификационный номер пользователя.
     * @return объект класса {@link UserDto}.
     */
    @Override
    public Optional<UserDto> findById(long userId) {
        var user = userMap.get(userId);

        log.info("Запрошен пользователь с id {}, данные {}", userId, user);

        return user != null ? Optional.of(userMapper.toUserDto(user, userId)) : Optional.empty();
    }

    /**
     * Метод удаления пользователя по id из репозитория.
     *
     * @param userId идентификационный номер пользователя.
     */
    @Override
    public void deleteById(long userId) {
        userMap.remove(userId);
        log.info("Удалён пользователь с id {}", userId);
    }

    /**
     * Метод получения списка всех пользователей из репозитория.
     *
     * @return {@link List} объектов {@link UserDto}.
     */
    @Override
    public List<UserDto> findAll() {
        log.info("Запрошен список всех пользователей");
        return userMap.entrySet().stream()
                .map(e -> userMapper.toUserDto(e.getValue(), e.getKey()))
                .collect(Collectors.toList());
    }

    private void checkEmail(User user, String message) {
        Optional<User> first = userMap.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .findFirst();
        if (first.isPresent()) {
            throw new UserRepositoryException(message);
        }
    }
}
