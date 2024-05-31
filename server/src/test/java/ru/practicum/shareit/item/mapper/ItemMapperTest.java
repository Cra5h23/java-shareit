package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.BookingShort;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class ItemMapperTest {

    private Item item;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(1L)
                .available(true)
                .description("testDescription")
                .name("testName")
                .owner(User.builder()
                        .id(23L)
                        .email("testOwner@email.com")
                        .name("testOwner")
                        .build())
                .build();
    }

    @Test
    void toItemResponseDtoTest() {
        var test = ItemMapper.toItemResponseDto(item);

        Assertions.assertNotNull(test);
        Assertions.assertEquals(1, test.getId());
        Assertions.assertEquals("testDescription", test.getDescription());
        Assertions.assertNull(test.getRequestId());
        Assertions.assertEquals(true, test.getAvailable());
        Assertions.assertEquals("testName", test.getName());

    }

    @Test
    void testToItemResponseDtoTest() {
        var test = ItemMapper.toItemResponseDto(item, 2L);

        Assertions.assertNotNull(test);
        Assertions.assertEquals(1, test.getId());
        Assertions.assertEquals("testDescription", test.getDescription());
        Assertions.assertEquals(2, test.getRequestId());
        Assertions.assertEquals(true, test.getAvailable());
        Assertions.assertEquals("testName", test.getName());
    }

    @Test
    void toItemTest() {
        var itemDto = ItemDtoRequest.builder()
                .available(true)
                .description("testDescription")
                .name("testName")
                .requestId(23L)
                .build();

        var user = User.builder()
                .id(23L)
                .email("testOwner@email.com")
                .name("testOwner")
                .build();

        var test = ItemMapper.toItem(itemDto, user);

        Assertions.assertNotNull(test);
        Assertions.assertEquals("testDescription", test.getDescription());
        Assertions.assertEquals(user, test.getOwner());
        Assertions.assertNull(test.getComments());
        Assertions.assertEquals("testName", test.getName());
        Assertions.assertEquals(true, test.getAvailable());
        Assertions.assertNull(test.getId());
    }

    @Test
    void toOwnerItemResponseDtoTest() {
        var lastBooking = BookingShort.builder()
                .id(13L)
                .bookerId(25L)
                .build();

        var nextBooking = BookingShort.builder()
                .id(19L)
                .bookerId(21L)
                .build();

        var test = ItemMapper.toOwnerItemResponseDto(item, lastBooking, nextBooking);

        Assertions.assertNotNull(test);
        Assertions.assertEquals("testDescription", test.getDescription());
        Assertions.assertEquals(List.of(), test.getComments());
        Assertions.assertEquals(lastBooking, test.getLastBooking());
        Assertions.assertEquals(nextBooking, test.getNextBooking());
        Assertions.assertEquals("testName", test.getName());
        Assertions.assertEquals(true, test.getAvailable());
        Assertions.assertEquals(1, test.getId());
    }


}