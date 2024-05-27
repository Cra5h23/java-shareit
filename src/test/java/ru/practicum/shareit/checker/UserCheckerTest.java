package ru.practicum.shareit.checker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class UserCheckerTest {
    private UserChecker userChecker;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userChecker = new UserChecker(userRepository);
    }

    @Test
    void checkUserTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> userChecker.checkUser(1L, "Message"));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Message", throwable.getMessage());

        Mockito.verify(userRepository, Mockito.times(1))
                .findById(Mockito.anyLong());
    }
}