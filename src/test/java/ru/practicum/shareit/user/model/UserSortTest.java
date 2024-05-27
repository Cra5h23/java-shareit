package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.UserType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class UserSortTest {
    @Test
    void valueOfTest() {
        Assertions.assertEquals(UserSort.NONE, UserSort.valueOf("NONE"));
        Assertions.assertEquals(UserSort.NAME_DESC, UserSort.valueOf("NAME_DESC"));
        Assertions.assertEquals(UserSort.ID_ASC, UserSort.valueOf("ID_ASC"));
        Assertions.assertEquals(UserSort.ID_DESC, UserSort.valueOf("ID_DESC"));
        Assertions.assertEquals(UserSort.NAME_ASC, UserSort.valueOf("NAME_ASC"));
        Assertions.assertEquals(UserSort.EMAIL_ASC, UserSort.valueOf("EMAIL_ASC"));
        Assertions.assertEquals(UserSort.EMAIL_DESC, UserSort.valueOf("EMAIL_DESC"));

        Throwable throwable = Assertions.assertThrows(IllegalArgumentException.class, () -> UserSort.valueOf("sdad"));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("No enum constant ru.practicum.shareit.user.model.UserSort.sdad", throwable.getMessage());
    }
}