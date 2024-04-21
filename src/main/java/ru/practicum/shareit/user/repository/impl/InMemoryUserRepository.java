package ru.practicum.shareit.user.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.exeption.UserRepositoryException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса {@link UserRepository} для хранения пользователей в памяти компьютера.
 *
 * @author Nikolay Radzivon.
 */
@Repository
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    private Long generatorId = 0L;
    private final List<User> userList = new ArrayList<>();

    /**
     * Метод добавления нового пользователя в репозиторий.
     *
     * @param user объект класса {@link User} для добавления.
     * @return объект класса {@link UserResponseDto}.
     */
    @Override
    public UserResponseDto save(UserRequestDto user) {
        checkEmail(user, String.format(
                "Нельзя добавить нового пользователя, Пользователь с email %s уже существует", user.getEmail()));

        var id = ++generatorId;
        User u = UserMapper.toUser(user, id);

        userList.add(u);
        log.info("В репозиторий добавлен новый пользователь {} и присвоен id {}", u, id);
        return UserMapper.toUserResponseDto(u);
    }

    /**
     * Метод обновления пользователя по id в репозитории.
     *
     * @param user   объект класса {@link User} для обновления.
     * @param userId идентификационный номер пользователя.
     * @return объект класса {@link UserResponseDto}.
     */
    @Override
    public UserResponseDto update(UserRequestDto user, long userId) {
        User updateUser = userList.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new UserRepositoryException(
                        String.format("Не существует пользователя с id %d", userId)));

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

        return UserMapper.toUserResponseDto(updateUser);
    }

    /**
     * Метод получения пользователя по id из репозитория.
     *
     * @param userId идентификационный номер пользователя.
     * @return объект класса {@link UserResponseDto}.
     */
    @Override
    public Optional<UserResponseDto> findById(long userId) {
        log.info("Запрошен пользователь с id {}", userId);
        return userList.stream()
                .filter(user -> user.getId().equals(userId))
                .map(UserMapper::toUserResponseDto)
                .findFirst();
    }

    /**
     * Метод удаления пользователя по id из репозитория.
     *
     * @param userId идентификационный номер пользователя.
     */
    @Override
    public void deleteById(long userId) {
        userList.stream().filter(user -> user.getId().equals(userId)).findFirst().map(userList::remove);
        log.info("Удалён пользователь с id {}", userId);
    }

    /**
     * Метод получения списка всех пользователей из репозитория.
     *
     * @return {@link List} объектов {@link UserResponseDto}.
     */
    @Override
    public List<UserResponseDto> findAll() {
        log.info("Запрошен список всех пользователей");
        return userList.stream().map(UserMapper::toUserResponseDto).collect(Collectors.toList());
    }

    /**
     * Метод проверки, что email не зарегистрирован.
     * @param user объект класса {@link UserRequestDto}.
     * @param message Сообщение ошибки.
     */
    private void checkEmail(UserRequestDto user, String message) {
        Optional<User> first = userList.stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .findFirst();
        if (first.isPresent()) {
            throw new UserRepositoryException(message);
        }
    }
}
