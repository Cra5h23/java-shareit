package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.exception.BookingServiceException;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.GetBookingsParams;
import ru.practicum.shareit.exception.NotFoundBookingException;
import ru.practicum.shareit.exception.NotFoundItemException;
import ru.practicum.shareit.exception.NotFoundUserException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Nikolay Radzivon
 * @Date 23.05.2024
 */
@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookingService bookingService;

    @Autowired
    ObjectMapper objectMapper;

    private final String xSharerUserId = "X-Sharer-User-Id";

    @Test
    @DisplayName("POST /bookings создаёт новое бронирование")
    void addNewBookingTestValid() throws Exception {
        var now = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().plusDays(2);
        var s = objectMapper.writeValueAsString(BookingRequestDto.builder()
                .itemId(1L)
                .start(now)
                .end(end)
                .build());

        var request = MockMvcRequestBuilders
                .post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(xSharerUserId, 1)
                .content(s);

        Mockito.when(bookingService.addNewBooking(Mockito.any(BookingRequestDto.class), Mockito.anyLong(), Mockito.any(TimeZone.class)))
                .thenReturn(BookingResponseDto.builder()
                        .id(1L)
                        .start(now.toString())
                        .end(end.toString())
                        .item(ItemBookingDto.builder()
                                .id(1L)
                                .name("TestItem")
                                .build())
                        .booker(BookerDto.builder()
                                .id(1L)
                                .build())
                        .status(BookingStatus.WAITING)
                        .build());

        this.mockMvc.perform(request).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.id").value(1),
                jsonPath("$.start").value(now.toString()),
                jsonPath("$.end").value(end.toString()),
                jsonPath("$.status").value("WAITING"),
                jsonPath("$.booker.id").value(1),
                jsonPath("$.item.id").value(1),
                jsonPath("$.item.name").value("TestItem")
        );

        Mockito.verify(bookingService, Mockito.times(1)).addNewBooking(Mockito.any(BookingRequestDto.class), Mockito.anyLong(), Mockito.any(TimeZone.class));
    }

    @Test
    @DisplayName("POST /bookings  не создаёт новое бронирование для не существующего пользователя")
    void addNewBookingTestNotValidUserNotExists() throws Exception {
        var now = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().plusDays(2);
        var s = objectMapper.writeValueAsString(BookingRequestDto.builder()
                .itemId(1L)
                .start(now)
                .end(end)
                .build());

        var request = MockMvcRequestBuilders
                .post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(xSharerUserId, 1)
                .content(s);

        Mockito.when(bookingService.addNewBooking(Mockito.any(BookingRequestDto.class), Mockito.anyLong(), Mockito.any(TimeZone.class)))
                .thenThrow(new NotFoundUserException("Нельзя забронировать вещь с id 1 для не существующего пользователя с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с бронированиями: Нельзя забронировать вещь с id 1 для не существующего пользователя с id 1"),
                jsonPath("$.path").value("/bookings")
        );

        Mockito.verify(bookingService, Mockito.times(1)).addNewBooking(Mockito.any(BookingRequestDto.class), Mockito.anyLong(), Mockito.any(TimeZone.class));
    }

    @Test
    @DisplayName("POST /bookings  не создаёт новое бронирование если вещь не существует")
    void addNewBookingTestNotValidItemNotExists() throws Exception {
        var now = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().plusDays(2);
        var s = objectMapper.writeValueAsString(BookingRequestDto.builder()
                .itemId(1L)
                .start(now)
                .end(end)
                .build());

        var request = MockMvcRequestBuilders
                .post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(xSharerUserId, 1)
                .content(s);

        Mockito.when(bookingService.addNewBooking(Mockito.any(BookingRequestDto.class), Mockito.anyLong(), Mockito.any(TimeZone.class)))
                .thenThrow(new NotFoundItemException("Нельзя забронировать не существующую вещь с id 1 для пользователя с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с бронированиями: Нельзя забронировать не существующую вещь с id 1 для пользователя с id 1"),
                jsonPath("$.path").value("/bookings")
        );

        Mockito.verify(bookingService, Mockito.times(1)).addNewBooking(Mockito.any(BookingRequestDto.class), Mockito.anyLong(), Mockito.any(TimeZone.class));
    }

    @Test
    @DisplayName("POST /bookings  не создаёт новое бронирование если вещь не доступна для бронирования")
    void addNewBookingTestNotValidItemNotAvailable() throws Exception {
        var now = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().plusDays(2);
        var s = objectMapper.writeValueAsString(BookingRequestDto.builder()
                .itemId(1L)
                .start(now)
                .end(end)
                .build());

        var request = MockMvcRequestBuilders
                .post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(xSharerUserId, 1)
                .content(s);

        Mockito.when(bookingService.addNewBooking(Mockito.any(BookingRequestDto.class), Mockito.anyLong(), Mockito.any(TimeZone.class)))
                .thenThrow(new BookingServiceException("Нельзя забронировать не доступную для бронирования вещь с id 1 для пользователя с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка работы с бронированиями: Нельзя забронировать не доступную для бронирования вещь с id 1 для пользователя с id 1"),
                jsonPath("$.path").value("/bookings")
        );

        Mockito.verify(bookingService, Mockito.times(1)).addNewBooking(Mockito.any(BookingRequestDto.class), Mockito.anyLong(), Mockito.any(TimeZone.class));
    }

    @Test
    @DisplayName("POST /bookings  не создаёт новое бронирование если пользователь является владельцем вещи")
    void addNewBookingTestNotValidUserIsOwnerItem() throws Exception {
        var now = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().plusDays(2);
        var s = objectMapper.writeValueAsString(BookingRequestDto.builder()
                .itemId(1L)
                .start(now)
                .end(end)
                .build());

        var request = MockMvcRequestBuilders
                .post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(xSharerUserId, 1)
                .content(s);

        Mockito.when(bookingService.addNewBooking(Mockito.any(BookingRequestDto.class), Mockito.anyLong(), Mockito.any(TimeZone.class)))
                .thenThrow(new NotFoundBookingException("Нельзя забронировать вещь c id 1 пользователь с id 1 является владельцем вещи"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с бронированиями: Нельзя забронировать вещь c id 1 пользователь с id 1 является владельцем вещи"),
                jsonPath("$.path").value("/bookings")
        );

        Mockito.verify(bookingService, Mockito.times(1)).addNewBooking(Mockito.any(BookingRequestDto.class), Mockito.anyLong(), Mockito.any(TimeZone.class));
    }

    @Test
    @DisplayName("POST /bookings  не создаёт новое бронирование если дата начала бронирования пуста")
    void addNewBookingTestNotValidStartTimeEmpty() throws Exception {
        LocalDateTime now = null;
        var end = LocalDateTime.now().plusDays(2);
        var s = objectMapper.writeValueAsString(BookingRequestDto.builder()
                .itemId(1L)
                .start(now)
                .end(end)
                .build());

        var request = MockMvcRequestBuilders
                .post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(xSharerUserId, 1)
                .content(s);

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных бронирования: Дата начала бронирования не может быть пустой"),
                jsonPath("$.path").value("/bookings")
        );
    }

    @Test
    @DisplayName("POST /bookings  не создаёт новое бронирование если дата окончания бронирования пуста")
    void addNewBookingTestNotValidEndTimeEmpty() throws Exception {
        var now = LocalDateTime.now().plusDays(1);
        LocalDateTime end = null;
        var s = objectMapper.writeValueAsString(BookingRequestDto.builder()
                .itemId(1L)
                .start(now)
                .end(end)
                .build());

        var request = MockMvcRequestBuilders
                .post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(xSharerUserId, 1)
                .content(s);

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных бронирования: Дата окончания бронирования не может быть пустой"),
                jsonPath("$.path").value("/bookings")
        );
    }

    @Test
    @DisplayName("POST /bookings  не создаёт новое бронирование если дата начала бронирования в прошлом")
    void addNewBookingTestNotValidStartTimeIsPast() throws Exception {
        var now = LocalDateTime.now().minusDays(1);
        var end = LocalDateTime.now().plusDays(2);
        var s = objectMapper.writeValueAsString(BookingRequestDto.builder()
                .itemId(1L)
                .start(now)
                .end(end)
                .build());

        var request = MockMvcRequestBuilders
                .post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(xSharerUserId, 1)
                .content(s);

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных бронирования: Дата начала бронирования не должна быть в прошлом"),
                jsonPath("$.path").value("/bookings")
        );
    }

    @Test
    @DisplayName("POST /bookings  не создаёт новое бронирование если дата окончания бронирования в прошлом")
    void addNewBookingTestNotValidEndTimeIsPast() throws Exception {
        var now = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().minusDays(2);
        var s = objectMapper.writeValueAsString(BookingRequestDto.builder()
                .itemId(1L)
                .start(now)
                .end(end)
                .build());

        var request = MockMvcRequestBuilders
                .post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(xSharerUserId, 1)
                .content(s);

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных бронирования: Дата окончания бронирования не должна быть в прошлом"),
                jsonPath("$.path").value("/bookings")
        );
    }

    @Test
    @DisplayName("POST /bookings  не создаёт новое бронирование если дата окончания бронирования раньше времени окончания бронирования")
    void addNewBookingTestNotValidEndTimeIsBeforeStartTime() throws Exception {
        var now = LocalDateTime.now().plusDays(2);
        var end = LocalDateTime.now().plusDays(1);
        var s = objectMapper.writeValueAsString(BookingRequestDto.builder()
                .itemId(1L)
                .start(now)
                .end(end)
                .build());

        var request = MockMvcRequestBuilders
                .post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(xSharerUserId, 1)
                .content(s);

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных бронирования: Дата окончания бронирования не должна быть раньше даты начала бронирования"),
                jsonPath("$.path").value("/bookings")
        );
    }

    @Test
    @DisplayName("POST /bookings  не создаёт новое бронирование если дата окончания бронирования равна времени начала бронирования")
    void addNewBookingTestNotValidEndTimeEqualsStartTime() throws Exception {
        var now = LocalDateTime.of(2024, 3,5,20,21,10);
        var s = objectMapper.writeValueAsString(BookingRequestDto.builder()
                .itemId(1L)
                .start(now)
                .end(now)
                .build());

        var request = MockMvcRequestBuilders
                .post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(xSharerUserId, 1)
                .content(s);

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных бронирования: Дата старта бронирования не должна совпадать с датой окончания бронирования"),
                jsonPath("$.path").value("/bookings")
        );
    }

    @Test
    @DisplayName("POST /bookings  не создаёт новое бронирование если не указан заголовок X-Sharer-User-Id")
    void addNewBookingTestNotValidXSharerUserIdNotExists() throws Exception {
        var now = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().plusDays(1);
        var s = objectMapper.writeValueAsString(BookingRequestDto.builder()
                .itemId(1L)
                .start(now)
                .end(end)
                .build());

        var request = MockMvcRequestBuilders
                .post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(s);

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан заголовок: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/bookings")
        );
    }

    @Test
    @DisplayName("PATCH /bookings/1?approved=true Подтверждает бронирование")
    void bookingConfirmationTestValidApprovedTrue() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/bookings/1")
                .param("approved", "true")
                .header(xSharerUserId, 1);
        var start = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().plusDays(2);

        Mockito.when(bookingService.bookingConfirmation(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.anyBoolean(), Mockito.any(TimeZone.class)))
                .thenReturn(BookingResponseDto.builder()
                        .id(1L)
                        .status(BookingStatus.APPROVED)
                        .booker(BookerDto.builder()
                                .id(1L)
                                .build())
                        .item(ItemBookingDto.builder()
                                .id(1L)
                                .name("TestItem")
                                .build())
                        .end(end.toString())
                        .start(start.toString())
                        .build());

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.id").value(1),
                jsonPath("$.start").value(start.toString()),
                jsonPath("$.end").value(end.toString()),
                jsonPath("$.status").value("APPROVED"),
                jsonPath("$.booker.id").value(1),
                jsonPath("$.item.id").value(1),
                jsonPath("$.item.name").value("TestItem")
        );

        Mockito.verify(bookingService, Mockito.times(1))
                .bookingConfirmation(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.anyBoolean(), Mockito.any(TimeZone.class));
    }

    @Test
    @DisplayName("PATCH /bookings/1?approved=false отклоняет бронирование")
    void bookingConfirmationTestValidApprovedFalse() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/bookings/1")
                .param("approved", "false")
                .header(xSharerUserId, 1);
        var start = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().plusDays(2);

        Mockito.when(bookingService.bookingConfirmation(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.anyBoolean(), Mockito.any(TimeZone.class)))
                .thenReturn(BookingResponseDto.builder()
                        .id(1L)
                        .status(BookingStatus.REJECTED)
                        .booker(BookerDto.builder()
                                .id(1L)
                                .build())
                        .item(ItemBookingDto.builder()
                                .id(1L)
                                .name("TestItem")
                                .build())
                        .end(end.toString())
                        .start(start.toString())
                        .build());

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.id").value(1),
                jsonPath("$.start").value(start.toString()),
                jsonPath("$.end").value(end.toString()),
                jsonPath("$.status").value("REJECTED"),
                jsonPath("$.booker.id").value(1),
                jsonPath("$.item.id").value(1),
                jsonPath("$.item.name").value("TestItem")
        );

        Mockito.verify(bookingService, Mockito.times(1))
                .bookingConfirmation(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.anyBoolean(), Mockito.any(TimeZone.class));
    }

    @Test
    @DisplayName("PATCH /bookings/1?approved если не указан параметр approved")
    void bookingConfirmationTestNotValidApprovedEmpty() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/bookings/1")
                .param("approved", "")
                .header(xSharerUserId, 1);

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан параметр запроса: Required request parameter 'approved' for method parameter type Boolean is present but converted to null"),
                jsonPath("$.path").value("/bookings/1")
        );
    }

    @Test
    @DisplayName("PATCH /bookings/1?approved=true если не указан заголовок x-Sharer-User-Id")
    void bookingConfirmationTestNotValidXSharerUserIdEmpty() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/bookings/1")
                .param("approved", "true")
                .header(xSharerUserId, "");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан заголовок: Required request header 'X-Sharer-User-Id' for method parameter type Long is present but converted to null"),
                jsonPath("$.path").value("/bookings/1")
        );
    }

    @Test
    @DisplayName("PATCH /bookings/1?approved=true не подтверждает бронирование когда оно не существует")
    void bookingConfirmationTestNotValidBookingNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/bookings/1")
                .param("approved", "true")
                .header(xSharerUserId, 1);
        var start = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().plusDays(2);

        Mockito.when(bookingService.bookingConfirmation(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.anyBoolean(), Mockito.any(TimeZone.class)))
                .thenThrow(new NotFoundBookingException("У пользователя с id 1 не существует запроса на бронирование с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с бронированиями: У пользователя с id 1 не существует запроса на бронирование с id 1"),
                jsonPath("$.path").value("/bookings/1")
        );

        Mockito.verify(bookingService, Mockito.times(1))
                .bookingConfirmation(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.anyBoolean(), Mockito.any(TimeZone.class));
    }

    @Test
    @DisplayName("PATCH /bookings/1?approved=true не подтверждает бронирование когда оно уже подтверждено")
    void bookingConfirmationTestNotValidBookingApproved() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/bookings/1")
                .param("approved", "true")
                .header(xSharerUserId, 1);
        var start = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().plusDays(2);

        Mockito.when(bookingService.bookingConfirmation(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.anyBoolean(), Mockito.any(TimeZone.class)))
                .thenThrow(new BookingServiceException("Владелец вещи с id 1 уже подтвердил бронирование с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка работы с бронированиями: Владелец вещи с id 1 уже подтвердил бронирование с id 1"),
                jsonPath("$.path").value("/bookings/1")
        );

        Mockito.verify(bookingService, Mockito.times(1))
                .bookingConfirmation(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.anyBoolean(), Mockito.any(TimeZone.class));
    }

    @Test
    @DisplayName("PATCH /bookings/1?approved=false не подтверждает бронирование когда оно уже подтверждено")
    void bookingConfirmationTestNotValidBookingReject() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/bookings/1")
                .param("approved", "false")
                .header(xSharerUserId, 1);
        var start = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().plusDays(2);

        Mockito.when(bookingService.bookingConfirmation(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.anyBoolean(), Mockito.any(TimeZone.class)))
                .thenThrow(new BookingServiceException("Владелец вещи с id 1 уже отклонил бронирование с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка работы с бронированиями: Владелец вещи с id 1 уже отклонил бронирование с id 1"),
                jsonPath("$.path").value("/bookings/1")
        );

        Mockito.verify(bookingService, Mockito.times(1))
                .bookingConfirmation(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.anyBoolean(), Mockito.any(TimeZone.class));
    }

    @Test
    @DisplayName("GET /bookings/1 возвращает бронирование")
    void getBookingTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/bookings/1")
                .header(xSharerUserId, 1);
        var start = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().plusDays(2);
        Mockito.when(bookingService.getBooking(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.any(TimeZone.class)))
                .thenReturn(BookingResponseDto.builder()
                        .id(1L)
                        .start(start.toString())
                        .end(end.toString())
                        .item(ItemBookingDto.builder()
                                .id(1L)
                                .name("TestItem")
                                .build())
                        .status(BookingStatus.WAITING)
                        .booker(BookerDto.builder()
                                .id(1L)
                                .build())
                        .build());

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.id").value(1),
                jsonPath("$.start").value(start.toString()),
                jsonPath("$.end").value(end.toString()),
                jsonPath("$.status").value("WAITING"),
                jsonPath("$.booker.id").value(1),
                jsonPath("$.item.id").value(1),
                jsonPath("$.item.name").value("TestItem")
        );

        Mockito.verify(bookingService, Mockito.times(1)).getBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(TimeZone.class));
    }

    @Test
    @DisplayName("GET /bookings/1 не возвращает бронирование когда оно не существует у пользователя")
    void getBookingTestNotValidBookingNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/bookings/1")
                .header(xSharerUserId, 1);
        var start = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().plusDays(2);
        Mockito.when(bookingService.getBooking(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.any(TimeZone.class)))
                .thenThrow(new NotFoundBookingException("У пользователя с id 1 не существует бронирования с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с бронированиями: У пользователя с id 1 не существует бронирования с id 1"),
                jsonPath("$.path").value("/bookings/1")
        );

        Mockito.verify(bookingService, Mockito.times(1)).getBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(TimeZone.class));
    }

    @Test
    @DisplayName("GET /bookings возвращает список из двух бронирований")
    void getBookingByUserTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/bookings")
                .header(xSharerUserId, 1)
                .param("state", "ALL")
                .param("from", "0")
                .param("size", "2");

        var start = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().plusDays(2);

        Mockito.when(bookingService.getBookingsByBooker(Mockito.any(GetBookingsParams.class)))
                .thenReturn(List.of(BookingResponseDto.builder()
                                .id(1L)
                                .booker(BookerDto.builder()
                                        .id(1L)
                                        .build())
                                .status(BookingStatus.APPROVED)
                                .item(ItemBookingDto.builder()
                                        .id(4L)
                                        .name("TestItem1")
                                        .build())
                                .start(start.plusDays(1).toString())
                                .end(end.plusDays(1).toString())
                                .build(),
                        BookingResponseDto.builder()
                                .id(2L)
                                .booker(BookerDto.builder()
                                        .id(1L)
                                        .build())
                                .status(BookingStatus.APPROVED)
                                .item(ItemBookingDto.builder()
                                        .id(3L)
                                        .name("TestItem2")
                                        .build())
                                .start(start.plusDays(2).toString())
                                .end(end.plusDays(2).toString())
                                .build()));

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.[0].id").value(1),
                jsonPath("$.[0].start").value(start.plusDays(1).toString()),
                jsonPath("$.[0].end").value(end.plusDays(1).toString()),
                jsonPath("$.[0].status").value("APPROVED"),
                jsonPath("$.[0].booker.id").value(1),
                jsonPath("$.[0].item.id").value(4),
                jsonPath("$.[0].item.name").value("TestItem1"),
                jsonPath("$.[1].id").value(2),
                jsonPath("$.[1].start").value(start.plusDays(2).toString()),
                jsonPath("$.[1].end").value(end.plusDays(2).toString()),
                jsonPath("$.[1].status").value("APPROVED"),
                jsonPath("$.[1].booker.id").value(1),
                jsonPath("$.[1].item.id").value(3),
                jsonPath("$.[1].item.name").value("TestItem2")
        );

        Mockito.verify(bookingService, Mockito.times(1)).getBookingsByBooker(Mockito.any(GetBookingsParams.class));
    }

    @Test
    @DisplayName("GET /bookings не возвращает список бронирований если пользователь не существует")
    void getBookingByUserTestNotValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/bookings")
                .header(xSharerUserId, 1)
                .param("state", "ALL")
                .param("from", "0")
                .param("size", "2");

        Mockito.when(bookingService.getBookingsByBooker(Mockito.any(GetBookingsParams.class)))
                .thenThrow(new NotFoundUserException("Нельзя получить список бронирований для не существующего пользователя с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с бронированиями: Нельзя получить список бронирований для не существующего пользователя с id 1"),
                jsonPath("$.path").value("/bookings")
        );

        Mockito.verify(bookingService, Mockito.times(1)).getBookingsByBooker(Mockito.any(GetBookingsParams.class));
    }

    @Test
    @DisplayName("GET /bookings не возвращает список бронирований если в параметре state указано не верное значение")
    void getBookingByUserTestNotValidStateASD() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/bookings")
                .header(xSharerUserId, 1)
                .param("state", "asd")
                .param("from", "0")
                .param("size", "2");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Unknown state: ASD"),
                jsonPath("$.path").value("/bookings")
        );
    }

    @Test
    @DisplayName("GET /bookings не возвращает список бронирований если в параметре from указано отрицательное значение")
    void getBookingByUserTestNotValidFrom() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/bookings")
                .header(xSharerUserId, 1)
                .param("state", "ALL")
                .param("from", "-1")
                .param("size", "2");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных бронирования: Параметр from не может быть меньше 0."),
                jsonPath("$.path").value("/bookings")
        );
    }

    @Test
    @DisplayName("GET /bookings не возвращает список бронирований если в параметре size указано отрицательное значение")
    void getBookingByUserTestNotValidSize() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/bookings")
                .header(xSharerUserId, 1)
                .param("state", "ALL")
                .param("from", "0")
                .param("size", "-1");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных бронирования: Параметр size не может быть меньше 0."),
                jsonPath("$.path").value("/bookings")
        );
    }

    @Test
    @DisplayName("GET /bookings не возвращает список бронирований если в параметре size указано значение больше 100")
    void getBookingByUserTestNotValidSize101() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/bookings")
                .header(xSharerUserId, 1)
                .param("state", "ALL")
                .param("from", "0")
                .param("size", "101");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных бронирования: Параметр size не может быть больше 100."),
                jsonPath("$.path").value("/bookings")
        );
    }

    @Test
    @DisplayName("GET /bookings/owner возвращает список из двух бронирований")
    void getBookingByOwnerTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/bookings/owner")
                .header(xSharerUserId, 1)
                .param("state", "ALL")
                .param("from", "0")
                .param("size", "2");

        var start = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().plusDays(2);

        Mockito.when(bookingService.getBookingByOwner(Mockito.any(GetBookingsParams.class)))
                .thenReturn(List.of(BookingResponseDto.builder()
                                .id(1L)
                                .booker(BookerDto.builder()
                                        .id(3L)
                                        .build())
                                .status(BookingStatus.APPROVED)
                                .item(ItemBookingDto.builder()
                                        .id(4L)
                                        .name("TestItem1")
                                        .build())
                                .start(start.plusDays(1).toString())
                                .end(end.plusDays(1).toString())
                                .build(),
                        BookingResponseDto.builder()
                                .id(2L)
                                .booker(BookerDto.builder()
                                        .id(5L)
                                        .build())
                                .status(BookingStatus.APPROVED)
                                .item(ItemBookingDto.builder()
                                        .id(3L)
                                        .name("TestItem2")
                                        .build())
                                .start(start.plusDays(2).toString())
                                .end(end.plusDays(2).toString())
                                .build()));

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.[0].id").value(1),
                jsonPath("$.[0].start").value(start.plusDays(1).toString()),
                jsonPath("$.[0].end").value(end.plusDays(1).toString()),
                jsonPath("$.[0].status").value("APPROVED"),
                jsonPath("$.[0].booker.id").value(3),
                jsonPath("$.[0].item.id").value(4),
                jsonPath("$.[0].item.name").value("TestItem1"),
                jsonPath("$.[1].id").value(2),
                jsonPath("$.[1].start").value(start.plusDays(2).toString()),
                jsonPath("$.[1].end").value(end.plusDays(2).toString()),
                jsonPath("$.[1].status").value("APPROVED"),
                jsonPath("$.[1].booker.id").value(5),
                jsonPath("$.[1].item.id").value(3),
                jsonPath("$.[1].item.name").value("TestItem2")
        );

        Mockito.verify(bookingService, Mockito.times(1)).getBookingByOwner(Mockito.any(GetBookingsParams.class));
    }

    @Test
    @DisplayName("GET /bookings/owner не возвращает список бронирований если пользователь не существует")
    void getBookingByOwnerTestNotValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/bookings/owner")
                .header(xSharerUserId, 1)
                .param("state", "ALL")
                .param("from", "0")
                .param("size", "2");

        Mockito.when(bookingService.getBookingByOwner(Mockito.any(GetBookingsParams.class)))
                .thenThrow(new NotFoundUserException("Нельзя получить список забронированных вещей для не существующего пользователя с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с бронированиями: Нельзя получить список забронированных вещей для не существующего пользователя с id 1"),
                jsonPath("$.path").value("/bookings/owner")
        );

        Mockito.verify(bookingService, Mockito.times(1)).getBookingByOwner(Mockito.any(GetBookingsParams.class));
    }

    @Test
    @DisplayName("GET /bookings/owner не возвращает список бронирований если в параметре state указано не верное значение")
    void getBookingByOwnerTestNotValidStateASD() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/bookings/owner")
                .header(xSharerUserId, 1)
                .param("state", "asd")
                .param("from", "0")
                .param("size", "2");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Unknown state: ASD"),
                jsonPath("$.path").value("/bookings/owner")
        );
    }

    @Test
    @DisplayName("GET /bookings/owner не возвращает список бронирований если в параметре from указано отрицательное значение")
    void getBookingByOwnerTestNotValidFrom() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/bookings/owner")
                .header(xSharerUserId, 1)
                .param("state", "ALL")
                .param("from", "-1")
                .param("size", "2");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных бронирования: Параметр from не может быть меньше 0."),
                jsonPath("$.path").value("/bookings/owner")
        );
    }

    @Test
    @DisplayName("GET /bookings/owner не возвращает список бронирований если в параметре size указано отрицательное значение")
    void getBookingByOwnerTestNotValidSize() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/bookings/owner")
                .header(xSharerUserId, 1)
                .param("state", "ALL")
                .param("from", "0")
                .param("size", "-1");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных бронирования: Параметр size не может быть меньше 0."),
                jsonPath("$.path").value("/bookings/owner")
        );
    }

    @Test
    @DisplayName("GET /bookings/owner не возвращает список бронирований если в параметре size указано значение больше 100")
    void getBookingByOwnerTestNotValidSize101() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/bookings/owner")
                .header(xSharerUserId, 1)
                .param("state", "ALL")
                .param("from", "0")
                .param("size", "101");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных бронирования: Параметр size не может быть больше 100."),
                jsonPath("$.path").value("/bookings/owner")
        );
    }


}