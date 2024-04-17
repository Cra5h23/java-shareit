package ru.practicum.shareit.user.exeption;

/**
 * Исключение для ошибок репозитория {@link UserRepositoryException}.
 *
 * @author Nikolay Radzivon.
 */
public class UserRepositoryException extends RuntimeException {
    public UserRepositoryException(String message) {
        super(message);
    }
}
