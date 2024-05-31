package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class UserTypeTest {
    @Test
    void valueOfTest() {
        Assertions.assertEquals(UserType.BOOKER, UserType.valueOf("BOOKER"));
        Assertions.assertEquals(UserType.OWNER, UserType.valueOf("OWNER"));

        Throwable throwable = Assertions.assertThrows(IllegalArgumentException.class, () -> UserType.valueOf("sdad"));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("No enum constant ru.practicum.shareit.booking.model.UserType.sdad", throwable.getMessage());
    }
}