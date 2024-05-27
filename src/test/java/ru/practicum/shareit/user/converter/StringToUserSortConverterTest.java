package ru.practicum.shareit.user.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.BookingStateException;
import ru.practicum.shareit.user.exeption.UserSortException;
import ru.practicum.shareit.user.model.UserSort;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class StringToUserSortConverterTest {
    private final StringToUserSortConverter converter = new StringToUserSortConverter();

    @Test
    void convertTest() {
        var none = converter.convert("none");
        Assertions.assertEquals(UserSort.NONE, none);

        var idAsc = converter.convert("id_asc");
        Assertions.assertEquals(UserSort.ID_ASC, idAsc);

        var idDesc = converter.convert("id_desc");
        Assertions.assertEquals(UserSort.ID_DESC, idDesc);

        var emailAsc = converter.convert("email_asc");
        Assertions.assertEquals(UserSort.EMAIL_ASC, emailAsc);

        var emailDesc = converter.convert("email_desc");
        Assertions.assertEquals(UserSort.EMAIL_DESC, emailDesc);

        var nameAsc = converter.convert("name_asc");
        Assertions.assertEquals(UserSort.NAME_ASC, nameAsc);

        var nameDesc = converter.convert("name_desc");
        Assertions.assertEquals(UserSort.NAME_DESC, nameDesc);
    }

    @Test
    void convertTestNotValid() {
        Throwable throwable = Assertions.assertThrows(UserSortException.class, () -> converter.convert("asd"));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("ASD", throwable.getMessage());
    }
}