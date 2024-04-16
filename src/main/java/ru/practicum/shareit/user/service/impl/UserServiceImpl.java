package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * Реализация интерфейса {@link UserService}.
 *
 * @author Nikolay Radzivon.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    /**
     * Метод добавления нового пользователя.
     *
     * @param user объект класса {@link User}.
     * @return объект класса {@link UserDto}.
     */
    @Override
    public UserDto addNewUser(User user) {
        log.info("Добавление нового пользователя {}", user);
        return userRepository.save(user);
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
        log.info("Обновление пользователя с id {}, новые данные {}", userId, user);
        return userRepository.update(user, userId);
    }

    /**
     * Метод получения пользователя по его id.
     *
     * @param userId идентификационный номер пользователя.
     * @return объект класса {@link UserDto}.
     */
    @Override
    public UserDto getUser(long userId) {
        log.info("Получение пользователя с id {}", userId);
        return userRepository.findById(userId);
    }

    /**
     * Метод удаления пользователя по его id.
     *
     * @param userId идентификационный номер пользователя.
     */
    @Override
    public void deleteUser(long userId) {
        log.info("Удаление пользователя с id {}", userId);
        userRepository.deleteById(userId);
    }

    /**
     * Метод получения списка всех пользователей.
     *
     * @return {@link List} объектов {@link UserDto}.
     */
    @Override
    public List<UserDto> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return userRepository.findAll();
    }
}
