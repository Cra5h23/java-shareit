package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.exception.NotFoundItemRequestException;
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.request.dto.ItemFromItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreated;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Nikolay Radzivon
 * @Date 25.05.2024
 */
@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemRequestService itemRequestService;

    private final String xSharerUserId = "X-Sharer-User-Id";

    @Test
    @DisplayName("POST /requests создаёт новый запрос")
    void addNewRequestTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/requests")
                .header(xSharerUserId, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"testDescription\"}");

        var now = LocalDateTime.now();
        Mockito.when(itemRequestService.addNewRequest(Mockito.any(ItemRequestDtoRequest.class), Mockito.anyLong(), Mockito.any(TimeZone.class)))
                .thenReturn(ItemRequestDtoCreated.builder()
                        .id(1L)
                        .description("testDescription")
                        .created(now)
                        .build());

        this.mockMvc.perform(request).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.id").value(1),
                jsonPath("$.description").value("testDescription"),
                jsonPath("$.created").exists()
        );

        Mockito.verify(itemRequestService, Mockito.times(1)).addNewRequest(Mockito.any(ItemRequestDtoRequest.class), Mockito.anyLong(), Mockito.any(TimeZone.class));
    }

    @Test
    @DisplayName("POST /requests не создаёт новый запрос когда пользователь не существует")
    void addNewRequestTestNotValidUserNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/requests")
                .header(xSharerUserId, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"testDescription\"}");

        var now = LocalDateTime.now();
        Mockito.when(itemRequestService.addNewRequest(Mockito.any(ItemRequestDtoRequest.class), Mockito.anyLong(), Mockito.any(TimeZone.class)))
                .thenThrow(new NotFoundUserException("Нельзя создать новый запрос для не существующего пользователя с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с запросами: Нельзя создать новый запрос для не существующего пользователя с id 1"),
                jsonPath("$.path").value("/requests")
        );

        Mockito.verify(itemRequestService, Mockito.times(1)).addNewRequest(Mockito.any(ItemRequestDtoRequest.class), Mockito.anyLong(), Mockito.any(TimeZone.class));
    }

    @Test
    @DisplayName("POST /requests не создаёт новый запрос если не указан заголовок X-Sharer-User-Id")
    void addNewRequestTestNotValidXSharerUserIdNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"testDescription\"}");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан заголовок: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/requests")
        );
    }

    @Test
    @DisplayName("GET /requests возвращает список из двух бронирований")
    void getUserRequestsTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests")
                .header(xSharerUserId, 1);
        var now = LocalDateTime.now();

        Mockito.when(itemRequestService.getUserRequests(Mockito.anyLong()))
                .thenReturn(List.of(ItemRequestDtoResponse.builder()
                        .id(1L)
                        .description("TestDescription")
                        .created(now.minusDays(3))
                        .items(List.of(ItemFromItemRequest.builder()
                                .id(3L)
                                .requestId(1L)
                                .name("testItem1")
                                .description("testDescription1")
                                .available(true)
                                .build()))
                        .build(), ItemRequestDtoResponse.builder()
                        .id(2L)
                        .description("TestDescription2")
                        .created(now.minusDays(1))
                        .items(List.of(ItemFromItemRequest.builder()
                                .id(55L)
                                .requestId(1L)
                                .name("testItem2")
                                .description("testDescription2")
                                .available(true)
                                .build()))
                        .build()
                ));

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                jsonPath("$.[0].id").value("1"),
                jsonPath("$.[0].description").value("TestDescription"),
                jsonPath("$.[0].created").exists(),
                jsonPath("$.[0].items.[0].id").value(3),
                jsonPath("$.[0].items.[0].name").value("testItem1"),
                jsonPath("$.[0].items.[0].description").value("testDescription1"),
                jsonPath("$.[0].items.[0].requestId").value(1),
                jsonPath("$.[0].items.[0].available").value(true),
                jsonPath("$.[1].id").value("2"),
                jsonPath("$.[1].description").value("TestDescription2"),
                jsonPath("$.[1].created").exists(),
                jsonPath("$.[1].items.[0].id").value(55),
                jsonPath("$.[1].items.[0].name").value("testItem2"),
                jsonPath("$.[1].items.[0].description").value("testDescription2"),
                jsonPath("$.[1].items.[0].requestId").value(1),
                jsonPath("$.[1].items.[0].available").value(true)
        );

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getUserRequests(Mockito.anyLong());
    }

    @Test
    @DisplayName("GET /requests не возвращает список бронирований если пользователь не существует")
    void getUserRequestsTestNotValidUserNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests")
                .header(xSharerUserId, 1);
        var now = LocalDateTime.now();

        Mockito.when(itemRequestService.getUserRequests(Mockito.anyLong()))
                .thenThrow(new NotFoundUserException("Нельзя запросить список запросов для не существующего пользователя с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с запросами: Нельзя запросить список запросов для не существующего пользователя с id 1"),
                jsonPath("$.path").value("/requests")
        );

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getUserRequests(Mockito.anyLong());
    }

    @Test
    @DisplayName("GET /requests не возвращает список бронирований если не указан заголовок X-Sharer-User-Id")
    void getUserRequestsTestNotValidXSharerUserIdNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан заголовок: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/requests")
        );
    }

    @Test
    @DisplayName("GET /requests/all  возвращает список всех бронирований")
    void getAllRequestsTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests/all")
                .header(xSharerUserId, 1)
                .param("from", "0")
                .param("size", "2");

        var now = LocalDateTime.now();

        Mockito.when(itemRequestService.getAllRequests(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(ItemRequestDtoResponse.builder()
                        .id(1L)
                        .description("TestDescription")
                        .created(now.minusDays(3))
                        .items(List.of(ItemFromItemRequest.builder()
                                .id(3L)
                                .requestId(1L)
                                .name("testItem1")
                                .description("testDescription1")
                                .available(true)
                                .build()))
                        .build(), ItemRequestDtoResponse.builder()
                        .id(2L)
                        .description("TestDescription2")
                        .created(now.minusDays(1))
                        .items(List.of(ItemFromItemRequest.builder()
                                .id(55L)
                                .requestId(1L)
                                .name("testItem2")
                                .description("testDescription2")
                                .available(true)
                                .build()))
                        .build()
                ));

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                jsonPath("$.[0].id").value("1"),
                jsonPath("$.[0].description").value("TestDescription"),
                jsonPath("$.[0].created").exists(),
                jsonPath("$.[0].items.[0].id").value(3),
                jsonPath("$.[0].items.[0].name").value("testItem1"),
                jsonPath("$.[0].items.[0].description").value("testDescription1"),
                jsonPath("$.[0].items.[0].requestId").value(1),
                jsonPath("$.[0].items.[0].available").value(true),
                jsonPath("$.[1].id").value("2"),
                jsonPath("$.[1].description").value("TestDescription2"),
                jsonPath("$.[1].created").exists(),
                jsonPath("$.[1].items.[0].id").value(55),
                jsonPath("$.[1].items.[0].name").value("testItem2"),
                jsonPath("$.[1].items.[0].description").value("testDescription2"),
                jsonPath("$.[1].items.[0].requestId").value(1),
                jsonPath("$.[1].items.[0].available").value(true)
        );

        Mockito.verify(itemRequestService, Mockito.times(1)).getAllRequests(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    @DisplayName("GET /requests/all не возвращает список всех бронирований если пользователь не существует")
    void getAllRequestsTestNotValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests/all")
                .header(xSharerUserId, 1)
                .param("from", "0")
                .param("size", "2");

        Mockito.when(itemRequestService.getAllRequests(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new NotFoundUserException("Нельзя запросить список всех запросов от не существующего пользователя с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с запросами: Нельзя запросить список всех запросов от не существующего пользователя с id 1"),
                jsonPath("$.path").value("/requests/all")
        );

        Mockito.verify(itemRequestService, Mockito.times(1)).getAllRequests(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    @DisplayName("GET /requests/all  возвращает пустой список если параметр from не указан")
    void getAllRequestsTestValidFromNull() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests/all")
                .header(xSharerUserId, 1)
                .param("size", "2");

        Mockito.when(itemRequestService.getAllRequests(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of());

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().json("[]")
        );

        Mockito.verify(itemRequestService, Mockito.times(1)).getAllRequests(1L, null, 2);
    }

    @Test
    @DisplayName("GET /requests/all  возвращает пустой список если параметр size не указан")
    void getAllRequestsTestValidSizeNull() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests/all")
                .header(xSharerUserId, 1)
                .param("from", "0");

        Mockito.when(itemRequestService.getAllRequests(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of());

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().json("[]")
        );

        Mockito.verify(itemRequestService, Mockito.times(1)).getAllRequests(1L, 0, null);
    }

    @Test
    @DisplayName("GET /requests/all не возвращает список всех бронирований если не указан заголовок X-Sharer-User-Id")
    void getAllRequestsTestNotValidXSharerUserIdNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests/all");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан заголовок: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/requests/all")
        );
    }

    @Test
    @DisplayName("GET /requests/all не возвращает список бронирований если параметр from меньше нуля")
    void getAllRequestsTestValidFromMinus1() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests/all")
                .header(xSharerUserId, 1)
                .param("from", "-1")
                .param("size", "2");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных запроса: Параметр from не может быть меньше 0."),
                jsonPath("$.path").value("/requests/all")
        );
    }

    @Test
    @DisplayName("GET /requests/all не возвращает список бронирований если параметр size меньше нуля")
    void getAllRequestsTestValidSizeMinus1() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests/all")
                .header(xSharerUserId, 1)
                .param("from", "0")
                .param("size", "-2");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных запроса: Параметр size не может быть меньше 0."),
                jsonPath("$.path").value("/requests/all")
        );
    }

    @Test
    @DisplayName("GET /requests/all не возвращает список бронирований если параметр size меньше нуля")
    void getAllRequestsTestValidSize101() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests/all")
                .header(xSharerUserId, 1)
                .param("from", "0")
                .param("size", "101");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных запроса: Параметр size не может быть больше 100."),
                jsonPath("$.path").value("/requests/all")
        );
    }

    @Test
    @DisplayName("GET /requests/1 возвращает запрос")
    void getRequestByIdTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests/1")
                .header(xSharerUserId, 1);

        var now = LocalDateTime.now();

        Mockito.when(itemRequestService.getRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(ItemRequestDtoResponse.builder()
                        .id(1L)
                        .created(now.minusDays(2))
                        .description("testDescription")
                        .items(List.of(ItemFromItemRequest.builder()
                                .id(3L)
                                .requestId(1L)
                                .name("testItem1")
                                .description("testDescription1")
                                .available(true)
                                .build()))
                        .build());

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                jsonPath("$.id").value("1"),
                jsonPath("$.description").value("testDescription"),
                jsonPath("$.created").exists(),
                jsonPath("$.items.[0].id").value(3),
                jsonPath("$.items.[0].name").value("testItem1"),
                jsonPath("$.items.[0].description").value("testDescription1"),
                jsonPath("$.items.[0].requestId").value(1),
                jsonPath("$.items.[0].available").value(true)
        );

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getRequestById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    @DisplayName("GET /requests/1 не возвращает запрос если пользователь не существует")
    void getRequestByIdTestNotValidUserNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests/1")
                .header(xSharerUserId, 1);

        Mockito.when(itemRequestService.getRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundUserException("Нельзя получить запрос не существующим пользователем с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с запросами: Нельзя получить запрос не существующим пользователем с id 1"),
                jsonPath("$.path").value("/requests/1")
        );

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getRequestById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    @DisplayName("GET /requests/1 не возвращает запрос если запрос не существует")
    void getRequestByIdTestNotValidRequestNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests/1")
                .header(xSharerUserId, 1);

        Mockito.when(itemRequestService.getRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundItemRequestException("Нельзя получить не существующий запрос с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с запросами: Нельзя получить не существующий запрос с id 1"),
                jsonPath("$.path").value("/requests/1")
        );

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getRequestById(Mockito.anyLong(), Mockito.anyLong());
    }


    @Test
    @DisplayName("GET /requests/1 не возвращает бронирование если не указан заголовок X-Sharer-User-Id")
    void getRequestByIdTestNotValidXSharerUserIdNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests/1");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан заголовок: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/requests/1")
        );
    }
}