package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

/**
 * @author Nikolay Radzivon
 * @Date 28.05.2024
 */
class ItemResponseTest {
    @Test
    void createItemResponseTest() {
        var test = ItemResponse.builder()
                .id(1L)
                .item(Item.builder()
                        .owner(User.builder()
                                .id(1L)
                                .build())
                        .name("name")
                        .description("description")
                        .available(true)
                        .build())
                .request(ItemRequest.builder()
                        .id(2L)
                        .build())
                .build();

        int i = test.hashCode();
        Assertions.assertNotNull(i);
        Assertions.assertEquals(ItemResponse.class, test.getClass());
    }


    @Test
    void createItemResponseTestEquals() {
        var test = ItemResponse.builder()
                .id(1L)
                .item(Item.builder()
                        .owner(User.builder()
                                .id(1L)
                                .build())
                        .name("name")
                        .description("description")
                        .available(true)
                        .build())
                .request(ItemRequest.builder()
                        .id(2L)
                        .build())
                .build();

        var test1 = ItemResponse.builder()
                .id(1L)
                .item(Item.builder()
                        .owner(User.builder()
                                .id(1L)
                                .build())
                        .name("name")
                        .description("description")
                        .available(true)
                        .build())
                .request(ItemRequest.builder()
                        .id(2L)
                        .build())
                .build();


        Assertions.assertEquals(test, test1);
    }
}