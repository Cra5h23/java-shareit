package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.exeption.UserRepositoryException;
import ru.practicum.shareit.user.exeption.UserServiceException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserSort;
import ru.practicum.shareit.user.repository.UserRepository;
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
@Transactional
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
    @Transactional
    public UserResponseDto addNewUser(UserRequestDto user) {
        User u = UserMapper.toUser(user);

        log.info("Создание нового пользователя c данными {}", user);
        try {
            User save = userRepository.save(u);
            log.info("Присвоен id {}", save);
            return UserMapper.toUserResponseDto(save);
        } catch (Exception e) {
            throw new UserRepositoryException(String.format(
                    "Нельзя добавить нового пользователя, Пользователь с email %s уже существует", user.getEmail()));
        }
    }

    /**
     * Метод для обновления данных пользователей по id.
     *
     * @param user   объект класса {@link UserRequestDto}.
     * @param userId идентификационный номер пользователя.
     * @return обновлённый объект класса {@link UserResponseDto}.
     */
    @Override
    @Transactional
    public UserResponseDto updateUser(UserRequestDto user, long userId) {
        log.info("Обновление пользователя с id {}", userId);
        User u = checkUser(userId, String.format(checkUserErrorMessage, "обновить", userId));
        log.info("Старые данные {} новые данные {}", u, user);
        String email = user.getEmail();

        if (email != null) {
            u.setEmail(email);
        }

        String name = user.getName();

        if (name != null) {
            u.setName(name);
        }
        try {
            User save = userRepository.save(u);

            return UserMapper.toUserResponseDto(save);
        } catch (Exception e) {
            throw new UserRepositoryException(String.format(
                    "Нельзя обновить пользователя с id %d, Пользователь с email %s уже существует", userId, user.getEmail()));
        }
    }

    /**
     * Метод получения пользователя по его id.
     *
     * @param userId идентификационный номер пользователя.
     * @return объект класса {@link UserResponseDto}.
     */
    @Override
    @Transactional(readOnly = true)
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
    @Transactional
    public void deleteUser(long userId) {
        log.info("Удаление пользователя с id {}", userId);
        checkUser(userId, String.format(checkUserErrorMessage, "удалить", userId));
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers(int page, int size, UserSort sort) {
        log.info("Получение {} страницы списка всех пользователей размером {} записей и сортировкой {}", page, size, sort.name());
        Sort.TypedSort<User> s = Sort.sort(User.class);
        Sort a = s;
        switch (sort) {
            case NONE:
                break;
            case ID_ASC:
                a = s.by(User::getId).ascending();
                break;
            case ID_DESC:
                a = s.by(User::getId).descending();
                break;
            case NAME_DESC:
                a = s.by(User::getName).descending();
                break;
            case NAME_ASC:
                a = s.by(User::getName).ascending();
                break;
            case EMAIL_DESC:
                a = s.by(User::getEmail).descending();
                break;
            case EMAIL_ASC:
                a = s.by(User::getEmail).ascending();
                break;
        }

        Pageable pageable = PageRequest.of(page - 1, size, a);

        return userRepository.findAll(pageable)
                .map(UserMapper::toUserResponseDto)
                .stream()
                .collect(Collectors.toList());
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
