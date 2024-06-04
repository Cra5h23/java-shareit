package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Nikolay Radzivon
 * @Date 25.05.2024
 */
@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    private Item item;

    private User booker;

    @BeforeEach
    void setUp() {
        this.booker = createUser("testBooker", "testBooker@email.com");
        this.item = createItem("testItem", "testDescription", true,
                createUser("testOwner", "testOwner@email.com"));
    }

    private User createUser(String name, String email) {
        var user = User.builder()
                .name(name)
                .email(email)
                .build();

        return userRepository.save(user);
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        var i = Item.builder()
                .owner(owner)
                .description(description)
                .available(available)
                .name(name)
                .build();

        return itemRepository.save(i);
    }

    private Booking createBooking(User booker, Item item, BookingStatus status, ZonedDateTime start, ZonedDateTime end) {
        var b = Booking.builder()
                .booker(booker)
                .start(start)
                .end(end)
                .item(item)
                .status(status)
                .build();

        return bookingRepository.save(b);
    }

    @Test
    public void findBookingByIdAndUserIdTest() {
        var booking = createBooking(booker, item, BookingStatus.APPROVED, ZonedDateTime.now(),
                ZonedDateTime.now().plusDays(1));

        var bookingByIdAndUserId = bookingRepository.findBookingByIdAndUserId(booking.getId(),
                item.getOwner().getId());
        var b = bookingByIdAndUserId.get();

        Assertions.assertNotNull(b);
        Assertions.assertEquals(b.getId(), booking.getId());
        Assertions.assertEquals(b.getStart(), booking.getStart());
        Assertions.assertEquals(b.getEnd(), booking.getEnd());
        Assertions.assertEquals(b.getStatus(), booking.getStatus());
        Assertions.assertEquals(b.getItem(), booking.getItem());
        Assertions.assertEquals(b.getBooker(), booking.getBooker());
    }

    @Test
    public void findBookingByIdAndOwnerIdOrBookerIdTest() {
        var booking = createBooking(booker, item, BookingStatus.APPROVED, ZonedDateTime.now(),
                ZonedDateTime.now().plusDays(1));

        var bookingByOwnerId = bookingRepository.findBookingByIdAndOwnerIdOrBookerId(booking.getId(),
                this.item.getOwner().getId());

        var byOwner = bookingByOwnerId.get();

        Assertions.assertNotNull(byOwner);
        Assertions.assertEquals(byOwner.getId(), booking.getId());
        Assertions.assertEquals(byOwner.getStart(), booking.getStart());
        Assertions.assertEquals(byOwner.getEnd(), booking.getEnd());
        Assertions.assertEquals(byOwner.getStatus(), booking.getStatus());
        Assertions.assertEquals(byOwner.getItem(), booking.getItem());
        Assertions.assertEquals(byOwner.getBooker(), booking.getBooker());

        var bookingByBookerId = bookingRepository.findBookingByIdAndOwnerIdOrBookerId(booking.getId(), this.booker.getId());

        var byBooker = bookingByBookerId.get();
        Assertions.assertNotNull(byBooker);
        Assertions.assertEquals(byBooker.getId(), booking.getId());
        Assertions.assertEquals(byBooker.getStart(), booking.getStart());
        Assertions.assertEquals(byBooker.getEnd(), booking.getEnd());
        Assertions.assertEquals(byBooker.getStatus(), booking.getStatus());
        Assertions.assertEquals(byBooker.getItem(), booking.getItem());
        Assertions.assertEquals(byBooker.getBooker(), booking.getBooker());
    }

//    @Test
//    public void findByItem_IdAndBooker_IdTest() {
//        var booking = createBooking(booker, item, BookingStatus.APPROVED,
//                ZonedDateTime.now().minusDays(4), ZonedDateTime.now().minusDays(3));
//
//        var bookings = bookingRepository.findByItem_IdAndBooker_Id(this.item.getId(),
//                this.booker.getId(), ZonedDateTime.now());
//
//        var result = bookings.get(0);
//
//        Assertions.assertNotNull(bookings);
//        Assertions.assertEquals(bookings.size(), 1);
//        Assertions.assertEquals(booking.getId(), result.getId());
//        Assertions.assertEquals(booking.getItem(), result.getItem());
//        Assertions.assertEquals(booking.getBooker(), result.getBooker());
//        Assertions.assertEquals(booking.getStart(), result.getStart());
//        Assertions.assertEquals(booking.getEnd(), result.getEnd());
//        Assertions.assertEquals(booking.getStatus(), result.getStatus());
//    }

    @Test
    public void getBookingsTest() {
        var booking = createBooking(booker, item, BookingStatus.APPROVED,
                ZonedDateTime.now().minusDays(4), ZonedDateTime.now().minusDays(3));
        var booker2 = createUser("testBooker2", "testBooker2@email.com");
        var booking2 = createBooking(booker2, item, BookingStatus.APPROVED,
                ZonedDateTime.now().minusDays(2), ZonedDateTime.now().minusDays(1));
        var booker3 = createUser("testBooker3", "testBooker3@email.com");
        var booking3 = createBooking(booker3, item, BookingStatus.WAITING,
                ZonedDateTime.now().plusDays(1), ZonedDateTime.now().plusDays(2));

        var result = bookingRepository.getBookings(this.item.getId());

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(booking, result.get(0));
        Assertions.assertEquals(booking.getId(), result.get(0).getId());
        Assertions.assertEquals(booking.getItem(), result.get(0).getItem());
        Assertions.assertEquals(booking.getStart(), result.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), result.get(0).getEnd());
        Assertions.assertEquals(booking.getStatus(), result.get(0).getStatus());
        Assertions.assertEquals(booking.getBooker(), result.get(0).getBooker());

        Assertions.assertEquals(booking2, result.get(1));
        Assertions.assertEquals(booking2.getId(), result.get(1).getId());
        Assertions.assertEquals(booking2.getItem(), result.get(1).getItem());
        Assertions.assertEquals(booking2.getStart(), result.get(1).getStart());
        Assertions.assertEquals(booking2.getEnd(), result.get(1).getEnd());
        Assertions.assertEquals(booking2.getStatus(), result.get(1).getStatus());
        Assertions.assertEquals(booking2.getBooker(), result.get(1).getBooker());

        Assertions.assertEquals(booking3, result.get(2));
        Assertions.assertEquals(booking3.getId(), result.get(2).getId());
        Assertions.assertEquals(booking3.getItem(), result.get(2).getItem());
        Assertions.assertEquals(booking3.getStart(), result.get(2).getStart());
        Assertions.assertEquals(booking3.getEnd(), result.get(2).getEnd());
        Assertions.assertEquals(booking3.getStatus(), result.get(2).getStatus());
        Assertions.assertEquals(booking3.getBooker(), result.get(2).getBooker());
    }

    @Test
    public void getBookingsByItemsTest() {
        var booking = createBooking(booker, item, BookingStatus.APPROVED,
                ZonedDateTime.now().minusDays(4), ZonedDateTime.now().minusDays(3));
        var booking1 = createBooking(booker,
                createItem("testItem2", "testDescription2", true, createUser("testOwner2",
                        "testOwner2@email.com")),
                BookingStatus.APPROVED, ZonedDateTime.now().minusDays(4), ZonedDateTime.now().minusDays(3));
        var booking2 = createBooking(booker,
                createItem("testItem3", "testDescription3", true, createUser("testOwner3",
                        "testOwner3@email.com")),
                BookingStatus.APPROVED, ZonedDateTime.now().minusDays(2), ZonedDateTime.now().minusDays(1));

        var result = bookingRepository.getBookingsByItems(List.of(booking.getItem().getId(),
                booking1.getItem().getId(), booking2.getItem().getId()));

        Assertions.assertEquals(3, result.size());

        Assertions.assertEquals(booking, result.get(0));
        Assertions.assertEquals(booking.getId(), result.get(0).getId());
        Assertions.assertEquals(booking.getItem(), result.get(0).getItem());
        Assertions.assertEquals(booking.getStart(), result.get(0).getStart());
        Assertions.assertEquals(booking.getEnd(), result.get(0).getEnd());
        Assertions.assertEquals(booking.getStatus(), result.get(0).getStatus());
        Assertions.assertEquals(booking.getBooker(), result.get(0).getBooker());

        Assertions.assertEquals(booking1, result.get(1));
        Assertions.assertEquals(booking1.getId(), result.get(1).getId());
        Assertions.assertEquals(booking1.getItem(), result.get(1).getItem());
        Assertions.assertEquals(booking1.getStart(), result.get(1).getStart());
        Assertions.assertEquals(booking1.getEnd(), result.get(1).getEnd());
        Assertions.assertEquals(booking1.getStatus(), result.get(1).getStatus());
        Assertions.assertEquals(booking1.getBooker(), result.get(1).getBooker());

        Assertions.assertEquals(booking2, result.get(2));
        Assertions.assertEquals(booking2.getId(), result.get(2).getId());
        Assertions.assertEquals(booking2.getItem(), result.get(2).getItem());
        Assertions.assertEquals(booking2.getStart(), result.get(2).getStart());
        Assertions.assertEquals(booking2.getEnd(), result.get(2).getEnd());
        Assertions.assertEquals(booking2.getStatus(), result.get(2).getStatus());
        Assertions.assertEquals(booking2.getBooker(), result.get(2).getBooker());
    }
}