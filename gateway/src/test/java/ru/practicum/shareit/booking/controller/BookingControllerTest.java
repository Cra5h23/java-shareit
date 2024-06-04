package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Nikolay Radzivon
 * @Date 03.06.2024
 */
@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookingClient bookingClient;

    @Autowired
    ObjectMapper objectMapper;

    private final String xSharerUserId = "X-Sharer-User-Id";

    @Test
    @DisplayName("POST /bookings создаёт новое бронирование")
    void addNewBookingTestValid() throws Exception {
        var s = objectMapper.writeValueAsString(BookingRequestDto.builder()
                .itemId(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build());

        var request = MockMvcRequestBuilders
                .post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header(xSharerUserId, 1)
                .content(s);

        Mockito.when(bookingClient.bookItem(Mockito.anyLong(), Mockito.any(BookingRequestDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 9,\"start\": \"2024-06-03T16:04:33\", \"end\": \"2024-06-03T16:04:34\",\"status\": \"WAITING\",\"booker\": {\"id\": 1},\"item\": {\"id\": 2,\"name\": \"bus\"}}"));

        this.mockMvc.perform(request).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().string("{\"id\": 9,\"start\": \"2024-06-03T16:04:33\", \"end\": \"2024-06-03T16:04:34\",\"status\": \"WAITING\",\"booker\": {\"id\": 1},\"item\": {\"id\": 2,\"name\": \"bus\"}}")
        );

        Mockito.verify(bookingClient, Mockito.times(1)).bookItem(Mockito.anyLong(), Mockito.any(BookingRequestDto.class));
    }

    @Test
    @DisplayName("POST /bookings  не создаёт новое бронирование если дата начала бронирования пуста")
    void addNewBookingTestNotValidStartTimeEmpty() throws Exception {
        var end = LocalDateTime.now().plusDays(2);
        var s = objectMapper.writeValueAsString(BookingRequestDto.builder()
                .itemId(1L)
                .start(null)
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
        var s = objectMapper.writeValueAsString(BookingRequestDto.builder()
                .itemId(1L)
                .start(now)
                .end(null)
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
        var now = LocalDateTime.now().plusDays(1);
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

        Mockito.when(bookingClient.confirmationBooking(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong()))
                .thenReturn(
                        ResponseEntity
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("{\"id\": 9,\"start\": \"2024-06-03T16:04:33\", \"end\": \"2024-06-03T16:04:34\",\"status\": \"APPROVED\",\"booker\": {\"id\": 1},\"item\": {\"id\": 2,\"name\": \"bus\"}}"));


        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().string("{\"id\": 9,\"start\": \"2024-06-03T16:04:33\", \"end\": \"2024-06-03T16:04:34\",\"status\": \"APPROVED\",\"booker\": {\"id\": 1},\"item\": {\"id\": 2,\"name\": \"bus\"}}")
        );

        Mockito.verify(bookingClient, Mockito.times(1))
                .confirmationBooking(Mockito.anyLong(), Mockito.anyBoolean(),
                        Mockito.anyLong());
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
    @DisplayName("GET /bookings/1 возвращает бронирование")
    void getBookingTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/bookings/1")
                .header(xSharerUserId, 1);
        var start = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().plusDays(2);
        Mockito.when(bookingClient.getBooking(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(
                        ResponseEntity
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("{\"id\": 9,\"start\": \"2024-06-03T16:04:33\", \"end\": \"2024-06-03T16:04:34\",\"status\": \"APPROVED\",\"booker\": {\"id\": 1},\"item\": {\"id\": 2,\"name\": \"bus\"}}"));


        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().string("{\"id\": 9,\"start\": \"2024-06-03T16:04:33\", \"end\": \"2024-06-03T16:04:34\",\"status\": \"APPROVED\",\"booker\": {\"id\": 1},\"item\": {\"id\": 2,\"name\": \"bus\"}}")
        );

        Mockito.verify(bookingClient, Mockito.times(1)).getBooking(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    @DisplayName("PATCH /bookings/1?approved=true если не указан заголовок x-Sharer-User-Id")
    void getBookingTestNotValidXSharerUserIdEmpty() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/bookings/1")
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

        Mockito.when(bookingClient.getBookings(Mockito.anyLong(), Mockito.any(BookingState.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[{\"id\": 9,\"start\": \"2024-06-03T16:04:33\", \"end\": \"2024-06-03T16:04:34\",\"status\": \"APPROVED\",\"booker\": {\"id\": 1},\"item\": {\"id\": 2,\"name\": \"bus\"}},{\"id\": 10,\"start\": \"2024-06-04T16:04:33\", \"end\": \"2024-06-04T16:04:34\",\"status\": \"WATING\",\"booker\": {\"id\": 1},\"item\": {\"id\": 2,\"name\": \"car\"}}]"));

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().string("[{\"id\": 9,\"start\": \"2024-06-03T16:04:33\", \"end\": \"2024-06-03T16:04:34\",\"status\": \"APPROVED\",\"booker\": {\"id\": 1},\"item\": {\"id\": 2,\"name\": \"bus\"}},{\"id\": 10,\"start\": \"2024-06-04T16:04:33\", \"end\": \"2024-06-04T16:04:34\",\"status\": \"WATING\",\"booker\": {\"id\": 1},\"item\": {\"id\": 2,\"name\": \"car\"}}]")
        );

        Mockito.verify(bookingClient, Mockito.times(1)).getBookings(Mockito.anyLong(), Mockito.any(BookingState.class), Mockito.anyInt(), Mockito.anyInt());
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
                .header(xSharerUserId, 3)
                .param("state", "ALL")
                .param("from", "0")
                .param("size", "2");

        var start = LocalDateTime.now().plusDays(1);
        var end = LocalDateTime.now().plusDays(2);

        Mockito.when(bookingClient.getBookingsOwner(Mockito.anyLong(), Mockito.any(BookingState.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[{\"id\": 9,\"start\": \"2024-06-03T16:04:33\", \"end\": \"2024-06-03T16:04:34\",\"status\": \"APPROVED\",\"booker\": {\"id\": 1},\"item\": {\"id\": 2,\"name\": \"bus\"}},{\"id\": 10,\"start\": \"2024-06-04T16:04:33\", \"end\": \"2024-06-04T16:04:34\",\"status\": \"WATING\",\"booker\": {\"id\": 1},\"item\": {\"id\": 2,\"name\": \"car\"}}]"));

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().string("[{\"id\": 9,\"start\": \"2024-06-03T16:04:33\", \"end\": \"2024-06-03T16:04:34\",\"status\": \"APPROVED\",\"booker\": {\"id\": 1},\"item\": {\"id\": 2,\"name\": \"bus\"}},{\"id\": 10,\"start\": \"2024-06-04T16:04:33\", \"end\": \"2024-06-04T16:04:34\",\"status\": \"WATING\",\"booker\": {\"id\": 1},\"item\": {\"id\": 2,\"name\": \"car\"}}]")
        );

        Mockito.verify(bookingClient, Mockito.times(1)).getBookingsOwner(Mockito.anyLong(), Mockito.any(BookingState.class), Mockito.anyInt(), Mockito.anyInt());
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