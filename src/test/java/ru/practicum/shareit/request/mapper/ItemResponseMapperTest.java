package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class ItemResponseMapperTest {

    @Test
    void toItemResponseTest() {
        var item = Item.builder()
                .id(1L)
                .available(true)
                .description("testDescription")
                .name("testName")
                .owner(User.builder()
                        .id(12L)
                        .name("testUser")
                        .email("testUser@email.com")
                        .build())
                .build();

        var test = ItemResponseMapper.toItemResponse(item, 28L);

        Assertions.assertNotNull(test);
        Assertions.assertNull(test.getId());
        Assertions.assertEquals(item, test.getItem());
        Assertions.assertEquals(28, test.getRequest().getId());
    }
}