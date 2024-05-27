package ru.practicum.shareit.booking.service.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.BookingServiceException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.GetBookingsParams;
import ru.practicum.shareit.checker.ItemChecker;
import ru.practicum.shareit.checker.UserChecker;
import ru.practicum.shareit.exception.NotFoundBookingException;
import ru.practicum.shareit.exception.NotFoundItemException;
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

/**
 * @author Nikolay Radzivon
 * @Date 25.05.2024
 */

class BookingServiceImplTest {

    private BookingService bookingService;

    private BookingRepository bookingRepository;

    private UserChecker userChecker;

    private ItemChecker itemChecker;

    private User user;

    @BeforeEach
    void setUp() {
        bookingRepository = Mockito.mock(BookingRepository.class);
        userChecker = Mockito.mock(UserChecker.class);
        itemChecker = Mockito.mock(ItemChecker.class);

        bookingService = new BookingServiceImpl(bookingRepository, userChecker, itemChecker);

        user = User.builder()
                .id(1L)
                .name("TestUser")
                .email("testemail@test.com")
                .build();
    }

    @Test
    void addNewBookingTestValid() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(3L)
                        .name("testBooker")
                        .email("testBookerEmail@mail.com")
                        .build());

        Mockito.when(itemChecker.checkItem(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(Item.builder()
                        .id(1L)
                        .owner(user)
                        .name("TestItem")
                        .available(Boolean.TRUE)
                        .description("testDescription")
                        .build());

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .item(Item.builder()
                                .id(1L)
                                .owner(user)
                                .name("TestItem")
                                .available(Boolean.TRUE)
                                .description("testDescription")
                                .build())
                        .booker(User.builder()
                                .id(3L)
                                .name("testBooker")
                                .email("testBookerEmail@mail.com")
                                .build())
                        .end(ZonedDateTime.now().minusHours(2))
                        .start(ZonedDateTime.now().minusDays(1))
                        .build());

        LocalDateTime now = LocalDateTime.now();

        BookingResponseDto bookingResponseDto = bookingService.addNewBooking(BookingRequestDto.builder()
                .start(now.minusDays(1))
                .end(now.minusHours(2))
                .itemId(1L)
                .build(), 3L, TimeZone.getDefault());

        Assertions.assertNotNull(bookingResponseDto);
        Assertions.assertEquals(bookingResponseDto.getId(), 1);
        Assertions.assertEquals(bookingResponseDto.getStatus(), BookingStatus.WAITING);
        Assertions.assertEquals(bookingResponseDto.getItem().getId(), 1);
        Assertions.assertEquals(bookingResponseDto.getItem().getName(), "TestItem");
        Assertions.assertEquals(bookingResponseDto.getBooker().getId(), 3);

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemChecker, Mockito.times(1)).checkItem(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(bookingRepository, Mockito.times(1)).save(Mockito.any(Booking.class));
    }

    @Test
    void addNewBookingTestNotValidItemAvailableFalse() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(user);

        Mockito.when(itemChecker.checkItem(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(Item.builder()
                        .id(1L)
                        .owner(user)
                        .name("TestItem")
                        .available(Boolean.FALSE)
                        .description("testDescription")
                        .build());

        Throwable throwable = Assertions.assertThrows(BookingServiceException.class, () ->
                bookingService.addNewBooking(BookingRequestDto.builder()
                        .start(LocalDateTime.now().minusDays(1))
                        .end(LocalDateTime.now().minusHours(2))
                        .itemId(1L)
                        .build(), 3L, TimeZone.getDefault()));

        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals(throwable.getMessage(), "Нельзя забронировать не доступную для бронирования вещь с id 1 для пользователя с id 3");
        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemChecker, Mockito.times(1)).checkItem(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void addNewBookingTestNotValidBookerEqualsOwner() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testBooker")
                        .email("testBookerEmail@mail.com")
                        .build());

        Mockito.when(itemChecker.checkItem(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(Item.builder()
                        .id(1L)
                        .owner(user)
                        .name("TestItem")
                        .available(Boolean.TRUE)
                        .description("testDescription")
                        .build());

        Throwable throwable = Assertions.assertThrows(NotFoundBookingException.class, () ->
                bookingService.addNewBooking(BookingRequestDto.builder()
                        .start(LocalDateTime.now().minusDays(1))
                        .end(LocalDateTime.now().minusHours(2))
                        .itemId(1L)
                        .build(), 1L, TimeZone.getDefault()));

        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals(throwable.getMessage(), "Нельзя забронировать вещь c id 1 пользователь с id 1 является владельцем вещи");
        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemChecker, Mockito.times(1)).checkItem(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void addNewBookingTestNotValidNotFoundUser() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя забронировать вещь с id 1 для не существующего пользователя с id 3"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () ->
                bookingService.addNewBooking(BookingRequestDto.builder()
                        .start(LocalDateTime.now().minusDays(1))
                        .end(LocalDateTime.now().minusHours(2))
                        .itemId(1L)
                        .build(), 3L, TimeZone.getDefault()));

        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals(throwable.getMessage(), "Нельзя забронировать вещь с id 1 для не существующего пользователя с id 3");
        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void addNewBookingTestNotValidNotFoundItem() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(3L)
                        .name("testBooker")
                        .email("testBookerEmail@mail.com")
                        .build());

        Mockito.when(itemChecker.checkItem(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundItemException("Нельзя забронировать не существующую вещь с id 1 для пользователя с id 3"));

        Throwable throwable = Assertions.assertThrows(NotFoundItemException.class, () ->
                bookingService.addNewBooking(BookingRequestDto.builder()
                        .start(LocalDateTime.now().minusDays(1))
                        .end(LocalDateTime.now().minusHours(2))
                        .itemId(1L)
                        .build(), 3L, TimeZone.getDefault()));

        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals(throwable.getMessage(), "Нельзя забронировать не существующую вещь с id 1 для пользователя с id 3");
        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemChecker, Mockito.times(1)).checkItem(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void bookingConfirmationTestAPPROWED() {
        Mockito.when(bookingRepository.findBookingByIdAndUserId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .item(Item.builder()
                                .id(1L)
                                .owner(user)
                                .name("TestItem")
                                .available(Boolean.TRUE)
                                .description("testDescription")
                                .build())
                        .booker(User.builder()
                                .id(3L)
                                .name("testBooker")
                                .email("testBookerEmail@mail.com")
                                .build())
                        .end(ZonedDateTime.now().minusHours(2))
                        .start(ZonedDateTime.now().minusDays(1))
                        .build()));

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.APPROVED)
                        .item(Item.builder()
                                .id(1L)
                                .owner(user)
                                .name("TestItem")
                                .available(Boolean.TRUE)
                                .description("testDescription")
                                .build())
                        .booker(User.builder()
                                .id(3L)
                                .name("testBooker")
                                .email("testBookerEmail@mail.com")
                                .build())
                        .end(ZonedDateTime.now().minusHours(2))
                        .start(ZonedDateTime.now().minusDays(1))
                        .build());

        var bookingResponseDto = bookingService.bookingConfirmation(1L, 1L, true, TimeZone.getDefault());

        Assertions.assertNotNull(bookingResponseDto);
        Assertions.assertEquals(bookingResponseDto.getId(), 1);
        Assertions.assertEquals(bookingResponseDto.getItem().getId(), 1);
        Assertions.assertEquals(bookingResponseDto.getItem().getName(), "TestItem");
        Assertions.assertEquals(bookingResponseDto.getStatus(), BookingStatus.APPROVED);
        Assertions.assertEquals(bookingResponseDto.getBooker().getId(), 3);

        Mockito.verify(bookingRepository, Mockito.times(1)).findBookingByIdAndUserId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(bookingRepository, Mockito.times(1)).save(Mockito.any(Booking.class));
    }

    @Test
    void bookingConfirmationTestREJECTED() {
        Mockito.when(bookingRepository.findBookingByIdAndUserId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .item(Item.builder()
                                .id(1L)
                                .owner(user)
                                .name("TestItem")
                                .available(Boolean.TRUE)
                                .description("testDescription")
                                .build())
                        .booker(User.builder()
                                .id(3L)
                                .name("testBooker")
                                .email("testBookerEmail@mail.com")
                                .build())
                        .end(ZonedDateTime.now().minusHours(2))
                        .start(ZonedDateTime.now().minusDays(1))
                        .build()));

        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.REJECTED)
                        .item(Item.builder()
                                .id(1L)
                                .owner(user)
                                .name("TestItem")
                                .available(Boolean.TRUE)
                                .description("testDescription")
                                .build())
                        .booker(User.builder()
                                .id(3L)
                                .name("testBooker")
                                .email("testBookerEmail@mail.com")
                                .build())
                        .end(ZonedDateTime.now().minusHours(2))
                        .start(ZonedDateTime.now().minusDays(1))
                        .build());

        var bookingResponseDto = bookingService.bookingConfirmation(1L, 1L, false, TimeZone.getDefault());

        Assertions.assertNotNull(bookingResponseDto);
        Assertions.assertEquals(bookingResponseDto.getId(), 1);
        Assertions.assertEquals(bookingResponseDto.getItem().getId(), 1);
        Assertions.assertEquals(bookingResponseDto.getItem().getName(), "TestItem");
        Assertions.assertEquals(bookingResponseDto.getStatus(), BookingStatus.REJECTED);
        Assertions.assertEquals(bookingResponseDto.getBooker().getId(), 3);

        Mockito.verify(bookingRepository, Mockito.times(1)).findBookingByIdAndUserId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(bookingRepository, Mockito.times(1)).save(Mockito.any(Booking.class));
    }

    @Test
    void bookingConfirmationTestBookingNotFound() {
        Mockito.when(bookingRepository.findBookingByIdAndUserId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Throwable throwable = Assertions.assertThrows(NotFoundBookingException.class,
                () -> bookingService.bookingConfirmation(1L, 1L, true, TimeZone.getDefault()));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage(), "У пользователя с id 1 не существует запроса на бронирование с id 1");

        Mockito.verify(bookingRepository, Mockito.times(1)).findBookingByIdAndUserId(Mockito.anyLong(), Mockito.anyLong());
    }


    @Test
    void bookingConfirmationTestBookingApproved() {
        Mockito.when(bookingRepository.findBookingByIdAndUserId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.APPROVED)
                        .item(Item.builder()
                                .id(1L)
                                .owner(user)
                                .name("TestItem")
                                .available(Boolean.TRUE)
                                .description("testDescription")
                                .build())
                        .booker(User.builder()
                                .id(3L)
                                .name("testBooker")
                                .email("testBookerEmail@mail.com")
                                .build())
                        .end(ZonedDateTime.now().minusHours(2))
                        .start(ZonedDateTime.now().minusDays(1))
                        .build()));

        Throwable throwable = Assertions.assertThrows(BookingServiceException.class,
                () -> bookingService.bookingConfirmation(1L, 1L, true, TimeZone.getDefault()));

        Assertions.assertNotNull(throwable);
        Assertions.assertEquals(throwable.getMessage(), "Владелец вещи с id 1 уже подтвердил бронирование с id 1");

        Mockito.verify(bookingRepository, Mockito.times(1)).findBookingByIdAndUserId(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void bookingConfirmationTestBookingRejected() {
        Mockito.when(bookingRepository.findBookingByIdAndUserId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.REJECTED)
                        .item(Item.builder()
                                .id(1L)
                                .owner(user)
                                .name("TestItem")
                                .available(Boolean.TRUE)
                                .description("testDescription")
                                .build())
                        .booker(User.builder()
                                .id(3L)
                                .name("testBooker")
                                .email("testBookerEmail@mail.com")
                                .build())
                        .end(ZonedDateTime.now().minusHours(2))
                        .start(ZonedDateTime.now().minusDays(1))
                        .build()));

        Throwable throwable = Assertions.assertThrows(BookingServiceException.class,
                () -> bookingService.bookingConfirmation(1L, 1L, false, TimeZone.getDefault()));

        Assertions.assertNotNull(throwable);
        Assertions.assertEquals(throwable.getMessage(), "Владелец вещи с id 1 уже отклонил бронирование с id 1");

        Mockito.verify(bookingRepository, Mockito.times(1)).findBookingByIdAndUserId(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void getBookingTestValidByOwner() {
        Mockito.when(bookingRepository.findBookingByIdAndOwnerIdOrBookerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .item(Item.builder()
                                .id(1L)
                                .owner(user)
                                .name("TestItem")
                                .available(Boolean.TRUE)
                                .description("testDescription")
                                .build())
                        .booker(User.builder()
                                .id(3L)
                                .name("testBooker")
                                .email("testBookerEmail@mail.com")
                                .build())
                        .end(ZonedDateTime.now().minusHours(2))
                        .start(ZonedDateTime.now().minusDays(1))
                        .build()));

        var booking = bookingService.getBooking(1L, 1L, TimeZone.getDefault());

        Assertions.assertNotNull(booking);
        Assertions.assertEquals(booking.getId(), 1);
        Assertions.assertEquals(booking.getItem().getId(), 1);
        Assertions.assertEquals(booking.getItem().getName(), "TestItem");
        Assertions.assertEquals(booking.getStatus(), BookingStatus.WAITING);
        Assertions.assertEquals(booking.getBooker().getId(), 3);

        Mockito.verify(bookingRepository, Mockito.times(1)).findBookingByIdAndOwnerIdOrBookerId(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void getBookingTestValidByBooker() {
        Mockito.when(bookingRepository.findBookingByIdAndOwnerIdOrBookerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(Booking.builder()
                        .id(1L)
                        .status(BookingStatus.WAITING)
                        .item(Item.builder()
                                .id(1L)
                                .owner(user)
                                .name("TestItem")
                                .available(Boolean.TRUE)
                                .description("testDescription")
                                .build())
                        .booker(User.builder()
                                .id(3L)
                                .name("testBooker")
                                .email("testBookerEmail@mail.com")
                                .build())
                        .end(ZonedDateTime.now().minusHours(2))
                        .start(ZonedDateTime.now().minusDays(1))
                        .build()));

        var booking = bookingService.getBooking(1L, 3L, TimeZone.getDefault());

        Assertions.assertNotNull(booking);
        Assertions.assertEquals(booking.getId(), 1);
        Assertions.assertEquals(booking.getItem().getId(), 1);
        Assertions.assertEquals(booking.getItem().getName(), "TestItem");
        Assertions.assertEquals(booking.getStatus(), BookingStatus.WAITING);
        Assertions.assertEquals(booking.getBooker().getId(), 3);
        Mockito.verify(bookingRepository, Mockito.times(1)).findBookingByIdAndOwnerIdOrBookerId(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void getBookingTestNotValid() {
        Mockito.when(bookingRepository.findBookingByIdAndOwnerIdOrBookerId(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundBookingException("У пользователя с id 1 не существует бронирования с id 1"));

        Throwable throwable = Assertions.assertThrows(NotFoundBookingException.class,
                () -> bookingService.getBooking(1L, 3L, TimeZone.getDefault()));

        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals(throwable.getMessage(), "У пользователя с id 1 не существует бронирования с id 1");
        Mockito.verify(bookingRepository, Mockito.times(1)).findBookingByIdAndOwnerIdOrBookerId(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void getBookingsByBookerTestValidBookingStateAll() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testBooker")
                        .email("testBookerEmail@mail.com")
                        .build());
        Mockito.when(bookingRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class)))
                .thenReturn(Page.empty());

        List<BookingResponseDto> bookingsByBooker = bookingService.getBookingsByBooker(GetBookingsParams.builder()
                .userId(1L)
                .from(0)
                .size(2)
                .state(BookingState.ALL)
                .timeZone(TimeZone.getDefault())
                .build());

        Assertions.assertNotNull(bookingsByBooker);
        Assertions.assertEquals(bookingsByBooker.size(), 0);

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(bookingRepository, Mockito.times(1)).findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class));
    }

    @Test
    void getBookingsByBookerTestValidBookingStateWaiting() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testBooker")
                        .email("testBookerEmail@mail.com")
                        .build());
        Mockito.when(bookingRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class)))
                .thenReturn(Page.empty());

        List<BookingResponseDto> bookingsByBooker = bookingService.getBookingsByBooker(GetBookingsParams.builder()
                .userId(1L)
                .from(0)
                .size(2)
                .state(BookingState.WAITING)
                .timeZone(TimeZone.getDefault())
                .build());

        Assertions.assertNotNull(bookingsByBooker);
        Assertions.assertEquals(bookingsByBooker.size(), 0);

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(bookingRepository, Mockito.times(1)).findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class));
    }

    @Test
    void getBookingsByBookerTestValidBookingStateRejected() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testBooker")
                        .email("testBookerEmail@mail.com")
                        .build());
        Mockito.when(bookingRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class)))
                .thenReturn(Page.empty());

        List<BookingResponseDto> bookingsByBooker = bookingService.getBookingsByBooker(GetBookingsParams.builder()
                .userId(1L)
                .from(0)
                .size(2)
                .state(BookingState.REJECTED)
                .timeZone(TimeZone.getDefault())
                .build());

        Assertions.assertNotNull(bookingsByBooker);
        Assertions.assertEquals(bookingsByBooker.size(), 0);

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(bookingRepository, Mockito.times(1)).findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class));
    }

    @Test
    void getBookingsByBookerTestValidBookingStateCurrent() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testBooker")
                        .email("testBookerEmail@mail.com")
                        .build());
        Mockito.when(bookingRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class)))
                .thenReturn(Page.empty());

        List<BookingResponseDto> bookingsByBooker = bookingService.getBookingsByBooker(GetBookingsParams.builder()
                .userId(1L)
                .from(0)
                .size(2)
                .state(BookingState.CURRENT)
                .timeZone(TimeZone.getDefault())
                .build());

        Assertions.assertNotNull(bookingsByBooker);
        Assertions.assertEquals(bookingsByBooker.size(), 0);

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(bookingRepository, Mockito.times(1)).findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class));
    }

    @Test
    void getBookingsByBookerTestValidBookingStateFuture() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testBooker")
                        .email("testBookerEmail@mail.com")
                        .build());
        Mockito.when(bookingRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class)))
                .thenReturn(Page.empty());

        List<BookingResponseDto> bookingsByBooker = bookingService.getBookingsByBooker(GetBookingsParams.builder()
                .userId(1L)
                .from(0)
                .size(2)
                .state(BookingState.FUTURE)
                .timeZone(TimeZone.getDefault())
                .build());

        Assertions.assertNotNull(bookingsByBooker);
        Assertions.assertEquals(bookingsByBooker.size(), 0);

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(bookingRepository, Mockito.times(1)).findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class));
    }

    @Test
    void getBookingsByBookerTestValidBookingStatePast() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testBooker")
                        .email("testBookerEmail@mail.com")
                        .build());
        Mockito.when(bookingRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class)))
                .thenReturn(Page.empty());

        List<BookingResponseDto> bookingsByBooker = bookingService.getBookingsByBooker(GetBookingsParams.builder()
                .userId(1L)
                .from(0)
                .size(2)
                .state(BookingState.PAST)
                .timeZone(TimeZone.getDefault())
                .build());

        Assertions.assertNotNull(bookingsByBooker);
        Assertions.assertEquals(bookingsByBooker.size(), 0);

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(bookingRepository, Mockito.times(1)).findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class));
    }

    @Test
    void getBookingsByBookerTestNotValid() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя получить список бронирований для не существующего пользователя с id 1"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> bookingService.getBookingsByBooker(GetBookingsParams.builder()
                .userId(1L)
                .from(0)
                .size(2)
                .state(BookingState.WAITING)
                .timeZone(TimeZone.getDefault())
                .build()));

        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals(throwable.getMessage(), "Нельзя получить список бронирований для не существующего пользователя с id 1");

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void getBookingByOwner() {
    }


    void getBookingByOwnerTestValidBookingStateAll() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testBooker")
                        .email("testBookerEmail@mail.com")
                        .build());
        Mockito.when(bookingRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class)))
                .thenReturn(Page.empty());

        List<BookingResponseDto> bookingsByBooker = bookingService.getBookingByOwner(GetBookingsParams.builder()
                .userId(1L)
                .from(0)
                .size(2)
                .state(BookingState.ALL)
                .timeZone(TimeZone.getDefault())
                .build());

        Assertions.assertNotNull(bookingsByBooker);
        Assertions.assertEquals(bookingsByBooker.size(), 0);

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(bookingRepository, Mockito.times(1)).findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class));
    }

    @Test
    void getBookingByOwnerTestValidBookingStateWaiting() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testBooker")
                        .email("testBookerEmail@mail.com")
                        .build());
        Mockito.when(bookingRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class)))
                .thenReturn(Page.empty());

        List<BookingResponseDto> bookingsByBooker = bookingService.getBookingByOwner(GetBookingsParams.builder()
                .userId(1L)
                .from(0)
                .size(2)
                .state(BookingState.WAITING)
                .timeZone(TimeZone.getDefault())
                .build());

        Assertions.assertNotNull(bookingsByBooker);
        Assertions.assertEquals(bookingsByBooker.size(), 0);

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(bookingRepository, Mockito.times(1)).findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class));
    }

    @Test
    void getBookingByOwnerTestValidBookingStateRejected() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testBooker")
                        .email("testBookerEmail@mail.com")
                        .build());
        Mockito.when(bookingRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class)))
                .thenReturn(Page.empty());

        List<BookingResponseDto> bookingsByBooker = bookingService.getBookingByOwner(GetBookingsParams.builder()
                .userId(1L)
                .from(0)
                .size(2)
                .state(BookingState.REJECTED)
                .timeZone(TimeZone.getDefault())
                .build());

        Assertions.assertNotNull(bookingsByBooker);
        Assertions.assertEquals(bookingsByBooker.size(), 0);

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(bookingRepository, Mockito.times(1)).findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class));
    }

    @Test
    void getBookingByOwnerTestValidBookingStateCurrent() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testBooker")
                        .email("testBookerEmail@mail.com")
                        .build());
        Mockito.when(bookingRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class)))
                .thenReturn(Page.empty());

        List<BookingResponseDto> bookingsByBooker = bookingService.getBookingByOwner(GetBookingsParams.builder()
                .userId(1L)
                .from(0)
                .size(2)
                .state(BookingState.CURRENT)
                .timeZone(TimeZone.getDefault())
                .build());

        Assertions.assertNotNull(bookingsByBooker);
        Assertions.assertEquals(bookingsByBooker.size(), 0);

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(bookingRepository, Mockito.times(1)).findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class));
    }

    @Test
    void getBookingByOwnerTestValidBookingStateFuture() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testBooker")
                        .email("testBookerEmail@mail.com")
                        .build());
        Mockito.when(bookingRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class)))
                .thenReturn(Page.empty());

        List<BookingResponseDto> bookingsByBooker = bookingService.getBookingByOwner(GetBookingsParams.builder()
                .userId(1L)
                .from(0)
                .size(2)
                .state(BookingState.FUTURE)
                .timeZone(TimeZone.getDefault())
                .build());

        Assertions.assertNotNull(bookingsByBooker);
        Assertions.assertEquals(bookingsByBooker.size(), 0);

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(bookingRepository, Mockito.times(1)).findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class));
    }

    @Test
    void getBookingByOwnerTestValidBookingStatePast() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testBooker")
                        .email("testBookerEmail@mail.com")
                        .build());
        Mockito.when(bookingRepository.findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class)))
                .thenReturn(Page.empty());

        List<BookingResponseDto> bookingsByBooker = bookingService.getBookingByOwner(GetBookingsParams.builder()
                .userId(1L)
                .from(0)
                .size(2)
                .state(BookingState.PAST)
                .timeZone(TimeZone.getDefault())
                .build());

        Assertions.assertNotNull(bookingsByBooker);
        Assertions.assertEquals(bookingsByBooker.size(), 0);

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(bookingRepository, Mockito.times(1)).findAll(Mockito.any(BooleanExpression.class), Mockito.any(PageRequest.class));
    }

    @Test
    void getBookingByOwnerTestNotValid() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя получить список бронирований для не существующего пользователя с id 1"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> bookingService.getBookingsByBooker(GetBookingsParams.builder()
                .userId(1L)
                .from(0)
                .size(2)
                .state(BookingState.WAITING)
                .timeZone(TimeZone.getDefault())
                .build()));

        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals(throwable.getMessage(), "Нельзя получить список бронирований для не существующего пользователя с id 1");

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
    }
}