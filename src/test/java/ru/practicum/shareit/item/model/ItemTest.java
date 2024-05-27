package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class ItemTest {
    @Test
    void createItemTest() {
        var item = Item.builder()
                .owner(User.builder()
                        .id(2L)
                        .email("testEmail2@mail.com")
                        .name("test2Name")
                        .build())
                .name("testItem")
                .description("testDescription")
                .available(true)
                .build();

        int i = item.hashCode();
        Assertions.assertNotNull(i);
    }

    @Test
    void createItemTestEquals() {
        var item = Item.builder()
                .id(1L)
                .owner(User.builder()
                        .id(2L)
                        .email("testEmail2@mail.com")
                        .name("test2Name")
                        .build())
                .name("testItem")
                .description("testDescription")
                .available(true)
                .build();

        var item1 = Item.builder()
                .id(1L)
                .owner(User.builder()
                        .id(2L)
                        .email("testEmail2@mail.com")
                        .name("test2Name")
                        .build())
                .name("testItem")
                .description("testDescription")
                .available(true)
                .build();


        Assertions.assertEquals(item, item1);
    }
}