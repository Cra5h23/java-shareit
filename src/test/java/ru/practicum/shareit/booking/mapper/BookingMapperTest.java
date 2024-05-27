package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class BookingMapperTest {

    @Test
    void toBookingTest() {
        var booking = BookingRequestDto.builder()
                .start(LocalDateTime.of(2024, 3, 15, 20, 15))
                .end(LocalDateTime.of(2024, 3, 16, 20, 15))
                .itemId(1L)
                .build();
        var user = User.builder()
                .id(33L)
                .name("TestUser")
                .email("TestUserEmail@mail.com")
                .build();
        var item = Item.builder()
                .id(22L)
                .owner(User.builder()
                        .id(15L)
                        .name("TestUser15")
                        .email("TestUserEmail15@mail.com")
                        .build())
                .name("Баржа")
                .description("Описание баржи")
                .available(true)
                .build();

        var test = BookingMapper.toBooking(booking, user, item, TimeZone.getDefault());

        assertNotNull(test);
        assertNull(test.getId());
        assertEquals(user, test.getBooker());
        assertEquals(item, test.getItem());
        assertEquals(BookingStatus.WAITING, test.getStatus());
        assertEquals(LocalDateTime.of(2024, 3, 15, 20, 15).atZone(ZoneId.systemDefault()), test.getStart());
        assertEquals(LocalDateTime.of(2024, 3, 16, 20, 15).atZone(ZoneId.systemDefault()), test.getEnd());
    }

    @Test
    void toBookingResponseDtoTest() {
        var user = User.builder()
                .id(33L)
                .name("TestUser")
                .email("TestUserEmail@mail.com")
                .build();
        var item = Item.builder()
                .id(22L)
                .owner(User.builder()
                        .id(15L)
                        .name("TestUser15")
                        .email("TestUserEmail15@mail.com")
                        .build())
                .name("Баржа")
                .description("Описание баржи")
                .available(true)
                .build();

        var booking = Booking.builder()
                .booker(user)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.of(2024, 3, 15, 20, 15).atZone(ZoneId.systemDefault()))
                .end(LocalDateTime.of(2024, 3, 16, 20, 15).atZone(ZoneId.systemDefault()))
                .item(item)
                .id(1L)
                .build();

        var test = BookingMapper.toBookingResponseDto(booking, TimeZone.getDefault());

        assertNotNull(test);
        assertEquals(33, test.getBooker().getId());
        assertEquals(1, test.getId());
        assertEquals("Баржа", test.getItem().getName());
        assertEquals(22, test.getItem().getId());
        assertEquals(BookingStatus.WAITING, test.getStatus());
        assertEquals(LocalDateTime.of(2024, 3, 15, 20, 15).toString(), test.getStart());
        assertEquals(LocalDateTime.of(2024, 3, 16, 20, 15).toString(), test.getEnd());
        assertEquals(33, test.getBooker().getId());
    }

    @Test
    void toBookingShortTest() {
        var user = User.builder()
                .id(33L)
                .name("TestUser")
                .email("TestUserEmail@mail.com")
                .build();
        var item = Item.builder()
                .id(22L)
                .owner(User.builder()
                        .id(15L)
                        .name("TestUser15")
                        .email("TestUserEmail15@mail.com")
                        .build())
                .name("Баржа")
                .description("Описание баржи")
                .available(true)
                .build();

        var booking = Booking.builder()
                .booker(user)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.of(2024, 3, 15, 20, 15).atZone(ZoneId.systemDefault()))
                .end(LocalDateTime.of(2024, 3, 16, 20, 15).atZone(ZoneId.systemDefault()))
                .item(item)
                .id(1L)
                .build();

        var test = BookingMapper.toBookingShort(booking);

        assertNotNull(test);
        assertEquals(1, test.getId());
        assertEquals(33, test.getBookerId());
    }
}