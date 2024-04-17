package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exeption.UserServiceException;
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
    private final String CHECK_USER_ERROR_MESSAGE = "Попытка %s пользователя с не существующим id %d";

    /**
     * Метод добавления нового пользователя.
     *
     * @param user объект класса {@link User}.
     * @return объект класса {@link UserDto}.
     */
    @Override
    public UserDto addNewUser(User user) {
        log.info("Создание нового пользователя {}", user);
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
        checkUser(userId, String.format(CHECK_USER_ERROR_MESSAGE,"обновить", userId));
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
        return checkUser(userId, String.format(CHECK_USER_ERROR_MESSAGE,"получить", userId));
    }

    /**
     * Метод удаления пользователя по его id.
     *
     * @param userId идентификационный номер пользователя.
     */
    @Override
    public void deleteUser(long userId) {
        log.info("Удаление пользователя с id {}", userId);
                checkUser(userId, String.format(CHECK_USER_ERROR_MESSAGE,"удалить", userId));
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

    /**
     * Метод для проверки существования пользователя.
     *
     * @param userId  идентификационный номер пользователя.
     * @param message сообщение ошибки если пользователь не существует.
     * @return объект класса {@link UserDto}
     */
    private UserDto checkUser(long userId, String message) {
        return userRepository.findById(userId).orElseThrow(() -> new UserServiceException(message));
    }
}
