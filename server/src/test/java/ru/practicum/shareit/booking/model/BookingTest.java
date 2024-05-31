package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class BookingTest {

    @Test
    void createBookingTesta() {
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

        int i = booking.hashCode();

        Assertions.assertNotNull(i);
    }

    @Test
    void createBookingTest() {
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

        var user1 = User.builder()
                .id(33L)
                .name("TestUser")
                .email("TestUserEmail@mail.com")
                .build();
        var item1 = Item.builder()
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

        var booking1 = Booking.builder()
                .booker(user)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.of(2024, 3, 15, 20, 15).atZone(ZoneId.systemDefault()))
                .end(LocalDateTime.of(2024, 3, 16, 20, 15).atZone(ZoneId.systemDefault()))
                .item(item)
                .id(1L)
                .build();

        Assertions.assertEquals(booking1, booking);
    }
}