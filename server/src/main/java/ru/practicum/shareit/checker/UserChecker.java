package ru.practicum.shareit.checker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

/**
 * @author Nikolay Radzivon
 * @Date 19.05.2024
 */
@Component
@RequiredArgsConstructor
public class UserChecker {
    private final UserRepository userRepository;

    public User checkUser(Long userId, String message) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(message));
    }
}
