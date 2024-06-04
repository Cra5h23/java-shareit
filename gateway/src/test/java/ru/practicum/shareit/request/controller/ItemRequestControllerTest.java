package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Nikolay Radzivon
 * @Date 03.06.2024
 */
@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemRequestClient itemRequestClient;

    private final String xSharerUserId = "X-Sharer-User-Id";

    @Test
    void addNewRequestTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/requests")
                .header(xSharerUserId, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"testDescription\"}");

        var now = LocalDateTime.now();
        Mockito.when(itemRequestClient.addItemRequest(Mockito.anyLong(), Mockito.any(ItemRequestDtoRequest.class)))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1,\"description\": \"testDescription\", \"created\": \"2024-05-26T19:34:00\"}"));

        this.mockMvc.perform(request).andExpectAll(
                status().isCreated(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.id").value(1),
                jsonPath("$.description").value("testDescription"),
                jsonPath("$.created").exists()
        );

        Mockito.verify(itemRequestClient, Mockito.times(1)).addItemRequest(Mockito.anyLong(), Mockito.any(ItemRequestDtoRequest.class));
    }

    @Test
    void addNewRequestTestNotValid() throws Exception {
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
    void addNewRequestTestNotValidDescriptionBlank() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .header(xSharerUserId, 1)
                .content("{\"description\": \"\"}");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных запроса: Описание требуемой вещи не может быть пустым"),
                jsonPath("$.path").value("/requests")
        );
    }

    @Test
    void addNewRequestTestNotValidDescription513() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .header(xSharerUserId, 1)
                .content("{\"description\": \"Проснувшись однажды утром после беспокойного сна, Грегор Замза обнаружил, что он у себя в постели превратился в страшное насекомое. Лежа на панцирнотвердой спине, он видел, стоило ему приподнять голову, свой коричневый, выпуклый, разделенный дугообразными чешуйками живот, на верхушке которого еле держалось готовое вот-вот окончательно сползти одеяло. Его многочисленные, убого тонкие по сравнению с остальным телом ножки беспомощно копошились у него перед глазами. «Что со мной случилось?» – подумал он. Это не.\"}");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных запроса: Описание требуемой вещи не может быть больше 512 символов"),
                jsonPath("$.path").value("/requests")
        );
    }


    @Test
    void getUserRequestsTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests")
                .header(xSharerUserId, 1);

        Mockito.when(itemRequestClient.getUserRequests(Mockito.anyLong()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[{\"id\": \"1\", \"description\": \"TestDescription\", \"created\": \"2024-05-29T20:03:20\", \"items\": [{\"id\": 3, \"name\": \"testItem1\", \"description\": \"testDescription1\", \"requestId\": 1, \"available\": true}]}, {\"id\": \"2\", \"description\": \"TestDescription2\", \"created\": \"2024-05-30T20:03:20\", \"items\": [{\"id\": 55, \"name\": \"testItem2\", \"description\": \"testDescription2\", \"requestId\": 1, \"available\": true}]}]"));

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
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

        Mockito.verify(itemRequestClient, Mockito.times(1))
                .getUserRequests(Mockito.anyLong());
    }

    @Test
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
    void getAllRequestsTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests/all")
                .header(xSharerUserId, 1)
                .param("from", "0")
                .param("size", "2");

        var now = LocalDateTime.now();

        Mockito.when(itemRequestClient.getAllRequests(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[{\"id\": \"1\", \"description\": \"TestDescription\", \"created\": \"2024-05-29T20:03:20\", \"items\": [{\"id\": 3, \"name\": \"testItem1\", \"description\": \"testDescription1\", \"requestId\": 1, \"available\": true}]}, {\"id\": \"2\", \"description\": \"TestDescription2\", \"created\": \"2024-05-30T20:03:20\", \"items\": [{\"id\": 55, \"name\": \"testItem2\", \"description\": \"testDescription2\", \"requestId\": 1, \"available\": true}]}]"));

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

        Mockito.verify(itemRequestClient, Mockito.times(1)).getAllRequests(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void getAllRequestsTestNotValidXSharerUserIdNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests/all")
                .param("from", "0")
                .param("size", "2");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан заголовок: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/requests/all")
        );
    }

    @Test
    void getAllRequestsTestNotValidFromMinus1() throws Exception {
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
    void getAllRequestsTestNotValidSizeMinus1() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests/all")
                .header(xSharerUserId, 1)
                .param("from", "0")
                .param("size", "-1");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных запроса: Параметр size не может быть меньше 0."),
                jsonPath("$.path").value("/requests/all")
        );
    }

    @Test
    void getAllRequestsTestNotValidSize101() throws Exception {
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
    void getRequestByIdTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/requests/1")
                .header(xSharerUserId, 1);

        var now = LocalDateTime.now();

        Mockito.when(itemRequestClient.getRequest(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1,\"description\": \"testDescription\",\"created\": \"2024-05-29T20:26:00\",\"items\": [{\"id\": 3,\"name\": \"testItem1\",\"description\": \"testDescription1\",\"requestId\": 1,\"available\": true}]}"));

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

        Mockito.verify(itemRequestClient, Mockito.times(1))
                .getRequest(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void getRequestByIdTestNotValidXSharerUserIdNotExists() throws Exception {
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
}