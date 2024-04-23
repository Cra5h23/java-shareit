package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.exeption.UserServiceException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
    private final String checkUserErrorMessage = "Попытка %s пользователя с не существующим id %d";

    /**
     * Метод добавления нового пользователя.
     *
     * @param user объект класса {@link UserRequestDto}.
     * @return объект класса {@link UserResponseDto}.
     */
    @Override
    public UserResponseDto addNewUser(UserRequestDto user) {
        User u = UserMapper.toUser(user);

        log.info("Создание нового пользователя {}", user);
        User save = userRepository.save(u);

        return UserMapper.toUserResponseDto(save);
    }

    /**
     * Метод для обновления данных пользователей по id.
     *
     * @param user   объект класса {@link UserRequestDto}.
     * @param userId идентификационный номер пользователя.
     * @return обновлённый объект класса {@link UserResponseDto}.
     */
    @Override
    public UserResponseDto updateUser(UserRequestDto user, long userId) {
        User u = checkUser(userId, String.format(checkUserErrorMessage, "обновить", userId));

        String email = user.getEmail();

        if (email != null) {
            u.setEmail(email);
        }

        String name = user.getName();

        if (name != null) {
            u.setName(name);
        }

        User save = userRepository.save(u);

        return UserMapper.toUserResponseDto(save);
    }

    /**
     * Метод получения пользователя по его id.
     *
     * @param userId идентификационный номер пользователя.
     * @return объект класса {@link UserResponseDto}.
     */
    @Override
    public UserResponseDto getUser(long userId) {
        log.info("Получение пользователя с id {}", userId);
        User user = checkUser(userId, String.format(checkUserErrorMessage, "получить", userId));

        return UserMapper.toUserResponseDto(user);
    }

    /**
     * Метод удаления пользователя по его id.
     *
     * @param userId идентификационный номер пользователя.
     */
    @Override
    public void deleteUser(long userId) {
        log.info("Удаление пользователя с id {}", userId);
        checkUser(userId, String.format(checkUserErrorMessage, "удалить", userId));
        userRepository.deleteById(userId);
    }

    /**
     * Метод получения списка всех пользователей.
     *
     * @return {@link List} объектов {@link UserResponseDto}.
     */
    @Override
    public List<UserResponseDto> getAllUsers() {
        log.info("Получение списка всех пользователей");
        Collection<User> all = userRepository.findAll();

        return all.stream().map(UserMapper::toUserResponseDto).collect(Collectors.toList());
    }

    /**
     * Метод для проверки существования пользователя.
     *
     * @param userId  идентификационный номер пользователя.
     * @param message сообщение ошибки если пользователь не существует.
     * @return объект класса {@link UserResponseDto}
     */
    private User checkUser(long userId, String message) {
        return userRepository.findById(userId).orElseThrow(() -> new UserServiceException(message));
    }
}
