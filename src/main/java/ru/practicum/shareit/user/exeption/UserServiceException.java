package ru.practicum.shareit.user.exeption;

/**
 * Исключение для ошибок сервиса {@link ru.practicum.shareit.user.service.UserService}
 *
 * @author Nikolqy Radzivon
 */
public class UserServiceException extends RuntimeException {
    public UserServiceException(String message) {
        super(message);
    }
}
