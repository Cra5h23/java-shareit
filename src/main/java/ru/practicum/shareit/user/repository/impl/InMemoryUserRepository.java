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

        log.info("Обновлён пользователь с id {} , старые данные {} новые данные {}", userId, updateUser, user);
        updateUser.setName(user.getName());
        updateUser.setEmail(user.getEmail());

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
        return Optional.of(userMapper.toUserDto(user, userId));
    }

    /**
     * Метод удаления пользователя по id из репозитория.
     *
     * @param userId идентификационный номер пользователя.
     */
    @Override
    public void deleteById(long userId) {

    }

    /**
     * Метод получения списка всех пользователей из репозитория.
     *
     * @return {@link List} объектов {@link UserDto}.
     */
    @Override
    public List<UserDto> findAll() {
        return List.of();
    }
}
