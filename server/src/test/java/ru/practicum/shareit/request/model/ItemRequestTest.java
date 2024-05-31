package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.ZonedDateTime;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class ItemRequestTest {

    @Test
    void createItemRequestTest() {
        var test = ItemRequest.builder()
                .id(1L)
                .requestor(User.builder()
                        .id(2L)
                        .email("testEmail2@mail.com")
                        .name("test2Name")
                        .build())
                .created(ZonedDateTime.now())
                .description("testDescription")
                .build();

        int i = test.hashCode();

        Assertions.assertNotNull(i);
    }

    @Test
    void createItemRequestTestEquals() {
        var test = ItemRequest.builder()
                .id(1L)
                .requestor(User.builder()
                        .id(2L)
                        .email("testEmail2@mail.com")
                        .name("test2Name")
                        .build())
                .created(ZonedDateTime.now())
                .description("testDescription")
                .build();

        var test1 = ItemRequest.builder()
                .id(1L)
                .requestor(User.builder()
                        .id(2L)
                        .email("testEmail2@mail.com")
                        .name("test2Name")
                        .build())
                .created(ZonedDateTime.now())
                .description("testDescription")
                .build();

        Assertions.assertEquals(test, test1);
    }
}