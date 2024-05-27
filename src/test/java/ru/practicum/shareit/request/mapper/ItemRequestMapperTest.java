package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemResponse;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class ItemRequestMapperTest {

    @Test
    void toItemRequestTest() {
        var request = ItemRequestDtoRequest.builder()
                .description("testDescription")
                .build();
        var user = User.builder()
                .id(12L)
                .name("testUser")
                .email("testUser@email.com")
                .build();

        var test = ItemRequestMapper.toItemRequest(request, user, TimeZone.getDefault());

        Assertions.assertNotNull(test);
        Assertions.assertNull(test.getId());
        Assertions.assertEquals("testDescription", test.getDescription());
        Assertions.assertEquals(user, test.getRequestor());
        Assertions.assertNotNull(test.getCreated());
    }

    @Test
    void toItemRequestDtoCreated() {
        var request = ItemRequest.builder()
                .id(1L)
                .created(ZonedDateTime.of(2024, 3, 13, 13, 23, 0, 0, ZoneId.systemDefault()))
                .requestor(User.builder()
                        .id(12L)
                        .name("testUser")
                        .email("testUser@email.com")
                        .build())
                .description("testDescription")
                .build();

        var test = ItemRequestMapper.toItemRequestDtoCreated(request);

        Assertions.assertNotNull(test);
        Assertions.assertEquals(LocalDateTime.of(2024, 3, 13, 13, 23, 0), test.getCreated());
        Assertions.assertEquals(1, test.getId());
        Assertions.assertEquals("testDescription", test.getDescription());
    }

    @Test
    void toItemRequestDtoResponse() {
        var request = ItemRequest.builder()
                .id(1L)
                .created(ZonedDateTime.of(2024, 3, 13, 13, 23, 0, 0, ZoneId.systemDefault()))
                .requestor(User.builder()
                        .id(12L)
                        .name("testUser")
                        .email("testUser@email.com")
                        .build())
                .description("testDescription")
                .responses(List.of(ItemResponse.builder()
                        .id(23L)
                        .item(Item.builder()
                                .id(13L)
                                .owner(User.builder()
                                        .id(3L)
                                        .name("testUser3")
                                        .email("testUser3@email.com")
                                        .build())
                                .name("testItem")
                                .description("testDescription")
                                .available(true)
                                .build())
                        .request(ItemRequest.builder().id(1L).build())
                        .build()))
                .build();

        var test = ItemRequestMapper.toItemRequestDtoResponse(request);

        Assertions.assertNotNull(test);
        Assertions.assertEquals(LocalDateTime.of(2024, 3, 13, 13, 23, 0), test.getCreated());
        Assertions.assertEquals(1, test.getId());
        Assertions.assertEquals("testDescription", test.getDescription());
        Assertions.assertEquals(13,test.getItems().get(0).getId());
        Assertions.assertEquals(1,test.getItems().get(0).getRequestId());
        Assertions.assertEquals("testDescription",test.getItems().get(0).getDescription());
        Assertions.assertEquals("testItem",test.getItems().get(0).getName());
        Assertions.assertEquals(true,test.getItems().get(0).getAvailable());
    }
}