package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Nikolay Radzivon
 * @Date 03.06.2024
 */
@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemClient itemClient;

    private final String xSharerUserId = "X-Sharer-User-Id";

    @Test
    @DisplayName("POST /items не создаёт новую вещь если не указан заголовок X-Sharer-User-Id")
    void addNewItem_X_SharerUser_Id_ExistTest() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testName1\"," +
                        "\"description\": \"testDescription1\"," +
                        "\"available\": true}");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан заголовок: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items")
        );
    }

    @Test
    @DisplayName("POST /items создаёт новую вещь")
    void addNewItemTest_Valid() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/items")
                .header(xSharerUserId, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Дрель\"," +
                        "\"description\": \"Простая дрель\"," +
                        "\"available\": true}");

        Mockito.when(itemClient.addItem(Mockito.any(), Mockito.any()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1, " +
                                "\"name\": \"Дрель\"," +
                                " \"description\": \"Простая дрель\"," +
                                " \"available\": true," +
                                " \"requestId\": null" +
                                "}"));

        this.mockMvc.perform(request).andExpectAll(
                status().isCreated(),
                jsonPath("$.name").value("Дрель"),
                jsonPath("$.id").value(1),
                jsonPath("$.description").value("Простая дрель"),
                jsonPath("$.available").value(true)
        );
        Mockito.verify(itemClient, Mockito.times(1)).addItem(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("POST /items не создаёт новую вещь если не указано имя вещи и создаёт форматированный ответ")
    void addNewItemTest_NotValidName() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/items")
                .header(xSharerUserId, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"testDescription1\"," +
                        "\"available\": true}");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных предмета: Название предмета не может быть пустым"),
                jsonPath("$.path").value("/items")
        );
    }

    @Test
    @DisplayName("POST /items не создаёт новую вещь если не указана доступность вещи для аренды и создаёт форматированный ответ")
    void addNewItemTest_NotValidAvailable() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/items")
                .header(xSharerUserId, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testName1\"," +
                        "\"description\": \"testDescription1\"" +
                        "}");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных предмета: Возможность аренды должна быть указана"),
                jsonPath("$.path").value("/items")
        );
    }

    @Test
    @DisplayName("POST /items не создаёт новую вещь если не указано описание вещи и создаёт форматированный ответ")
    void addNewItemTest_NotValidDescription() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/items")
                .header(xSharerUserId, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testName1\"," +
                        "\"available\": true}");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных предмета: Описание предмета не может быть пустым"),
                jsonPath("$.path").value("/items")
        );
    }

    @Test
    @DisplayName("PATCH /items/1 обновляет вещь")
    void updateItem_Valid() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/items/1")
                .header(xSharerUserId, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testName1update\"," +
                        "\"description\": \"testDescription1update\"," +
                        "\"available\": false}");

        Mockito.when(itemClient.updateItem(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"name\": \"testName1update\",\"id\": 1,\"description\": \"testDescription1update\",\"available\": false}"));

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                jsonPath("$.name").value("testName1update"),
                jsonPath("$.id").value(1),
                jsonPath("$.description").value("testDescription1update"),
                jsonPath("$.available").value(false)
        );
        Mockito.verify(itemClient, Mockito.times(1)).updateItem(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("PATCH /items/1 не обновляет вещь если не указан заголовок X-Sharer-User-Id")
    void updateItem_X_SharerUser_Id_ExistTest() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testName1\"," +
                        "\"description\": \"testDescription1\"," +
                        "\"available\": true}");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан заголовок: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items/1")
        );
    }

    @Test
    @DisplayName("GET /items/1 возвращает вещь")
    void getItemTestValid() throws Exception {
        var request = MockMvcRequestBuilders.get("/items/1")
                .header(xSharerUserId, 1);

        Mockito.when(itemClient.getItem(Mockito.any(), Mockito.any()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"name\": \"testName1\",\"id\": 1, \"description\": \"testDescription1\",\"available\": true}")
                );

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                jsonPath("$.name").value("testName1"),
                jsonPath("$.id").value(1),
                jsonPath("$.description").value("testDescription1"),
                jsonPath("$.available").value(true)
        );
        Mockito.verify(itemClient, Mockito.times(1)).getItem(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("PATCH /items/1 не обновляет вещь если не указан заголовок X-Sharer-User-Id")
    void getItemTestNotValidXSharerUserIdNotExist() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items/1");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан заголовок: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items/1")
        );
    }

    @Test
    void deleteItemByItemIdTestValid() throws Exception {
        var request = MockMvcRequestBuilders.delete("/items/1")
                .header(xSharerUserId, 1);

        Mockito.when(itemClient.deleteItem(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(""));

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON)
        );
        Mockito.verify(itemClient, Mockito.times(1)).deleteItem(Mockito.any(), Mockito.any());
    }

    @Test
    void deleteItemByItemIdTestNotValidXSharerUserIdNotExists() throws Exception {
        var request = MockMvcRequestBuilders.delete("/items/1");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан заголовок: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items/1")
        );
    }

    @Test
    void deleteAllItemByUserTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .delete("/items")
                .header(xSharerUserId, 1);

        Mockito.when(itemClient.deleteAllUserItems(Mockito.anyLong()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(""));

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON)
        );

        Mockito.verify(itemClient, Mockito.times(1))
                .deleteAllUserItems(Mockito.anyLong());
    }

    @Test
    void deleteAllItemByUserTestNotValidXSharerUserIdNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .delete("/items");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан заголовок: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items")
        );
    }

    @Test
    void getAllItemByUserTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items")
                .param("from", "0")
                .param("size", "2")
                .header(xSharerUserId, 1);

        Mockito.when(itemClient.getUserItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[{\"id\":1,\"name\":\"testName1\",\"description\":\"testDescription1\",\"available\":true,\"lastBooking\":{\"id\":1,\"bookerId\":1},\"nextBooking\":{\"id\":3,\"bookerId\":4},\"comments\":[]},{\"id\":2,\"name\":\"testName2\",\"description\":\"testDescription2\",\"available\":true,\"lastBooking\":{\"id\":null,\"bookerId\":null},\"nextBooking\":null,\"comments\":[]}]"));

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().json("[{\"id\":1,\"name\":\"testName1\",\"description\":\"testDescription1\",\"available\":true,\"lastBooking\":{\"id\":1,\"bookerId\":1},\"nextBooking\":{\"id\":3,\"bookerId\":4},\"comments\":[]},{\"id\":2,\"name\":\"testName2\",\"description\":\"testDescription2\",\"available\":true,\"lastBooking\":{\"id\":null,\"bookerId\":null},\"nextBooking\":null,\"comments\":[]}]")
        );
        Mockito.verify(itemClient, Mockito.times(1)).getUserItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void getAllItemByUserTestNotValidXSharerUserIdNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items")
                .param("from", "0")
                .param("size", "2");
        //.header(xSharerUserId, 1);

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан заголовок: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items")
        );
    }

    @Test
    void getAllItemByUserTestNotValidFromMinus1() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items")
                .param("from", "-1")
                .param("size", "2")
                .header(xSharerUserId, 1);

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных предмета: Параметр from не может быть меньше 0."),
                jsonPath("$.path").value("/items")
        );
    }

    @Test
    void getAllItemByUserTestNotValidSizeMinus1() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items")
                .param("from", "0")
                .param("size", "-1")
                .header(xSharerUserId, 1);

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных предмета: Параметр size не может быть меньше 0."),
                jsonPath("$.path").value("/items")
        );
    }

    @Test
    void getAllItemByUserTestNotValidSize101() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items")
                .param("from", "0")
                .param("size", "101")
                .header(xSharerUserId, 1);

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных предмета: Параметр size не может быть больше 100."),
                jsonPath("$.path").value("/items")
        );
    }

    @Test
    void getAllItemByUserTestValidFromNull() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items")
                .param("size", "2")
                .header(xSharerUserId, 1);

        Mockito.when(itemClient.getUserItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(List.of()));

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("[]")
        );
        Mockito.verify(itemClient, Mockito.times(1))
                .getUserItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void getAllItemByUserTestValidSizeNull() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items")
                .param("from", "0")
                .header(xSharerUserId, 1);

        Mockito.when(itemClient.getUserItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(List.of()));

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("[]")
        );
        Mockito.verify(itemClient, Mockito.times(1))
                .getUserItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void searchItemByTextTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items/search?text=test")
                .param("from", "0")
                .param("size", "2")
                .header(xSharerUserId, 1);

        Mockito.when(itemClient.searchItem(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[{\"id\":1,\"name\":\"testName1\",\"description\":\"testDescription1\",\"available\":true}," +
                                "{\"id\":2,\"name\":\"testName2\",\"description\":\"testDescription2\",\"available\":true}]"));

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().json("[{\"id\":1,\"name\":\"testName1\",\"description\":\"testDescription1\",\"available\":true}," +
                        "{\"id\":2,\"name\":\"testName2\",\"description\":\"testDescription2\",\"available\":true}]")
        );
        Mockito.verify(itemClient, Mockito.times(1)).searchItem(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void searchItemByTextTestNotValidTextBlank() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items/search?text=")
                .param("from", "0")
                .param("size", "2")
                .header(xSharerUserId, 1);

        Mockito.when(itemClient.searchItem(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("[]")
        );
        Mockito.verify(itemClient, Mockito.times(1)).searchItem(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void searchItemByTextTestNotValidFromMinus1() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items/search?text=")
                .param("from", "-1")
                .param("size", "2")
                .header(xSharerUserId, 1);

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных предмета: Параметр from не может быть меньше 0."),
                jsonPath("$.path").value("/items/search")
        );
    }

    @Test
    void searchItemByTextTestNotValidSizeMinus1() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items/search?text=test")
                .param("from", "0")
                .param("size", "-1")
                .header(xSharerUserId, 1);

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных предмета: Параметр size не может быть меньше 0."),
                jsonPath("$.path").value("/items/search")
        );
    }

    @Test
    void searchItemByTextTestNotValidSize101() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items/search?text=test")
                .param("from", "0")
                .param("size", "101")
                .header(xSharerUserId, 1);

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных предмета: Параметр size не может быть больше 100."),
                jsonPath("$.path").value("/items/search")
        );
    }

    @Test
    void searchItemByTextTestNotValidXSharerUserIdNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items/search?text=test")
                .param("from", "0")
                .param("size", "101");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан заголовок: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items/search")
        );
    }

    @Test
    void addCommentTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/items/1/comment")
                .header(xSharerUserId, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"testComment\"}");

        Mockito.when(itemClient.addComment(
                        Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.any(CommentRequestDto.class)))
                .thenReturn(
                        ResponseEntity
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("{\"id\": 1,\"text\": \"testComment\",\"authorName\": \"testAuthor\",\"created\": \"2024-05-23T09:59:40\"}"));


        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("{\"id\":1,\"text\":\"testComment\",\"authorName\":\"testAuthor\",\"created\":\"2024-05-23T09:59:40\"}")
        );
        Mockito.verify(itemClient, Mockito.times(1)).addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentRequestDto.class));
    }

    @Test
    void addCommentTestNotValidTextBlank() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/items/1/comment")
                .header(xSharerUserId, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"\"}");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных предмета: Текст комментария не может быть пустым"),
                jsonPath("$.path").value("/items/1/comment")
        );
    }

    @Test
    void addCommentTestNotValidText513() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/items/1/comment")
                .header(xSharerUserId, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"Проснувшись однажды утром после беспокойного сна, Грегор Замза обнаружил, что он у себя в постели превратился в страшное насекомое. Лежа на панцирнотвердой спине, он видел, стоило ему приподнять голову, свой коричневый, выпуклый, разделенный дугообразными чешуйками живот, на верхушке которого еле держалось готовое вот-вот окончательно сползти одеяло. Его многочисленные, убого тонкие по сравнению с остальным телом ножки беспомощно копошились у него перед глазами. «Что со мной случилось?» – подумал он. Это не.\"}");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных предмета: Текст комментария не может быть больше 512 символов"),
                jsonPath("$.path").value("/items/1/comment")
        );
    }

    @Test
    void addCommentTestNotValidXSharerUserId() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/items/1/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"test\"}");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан заголовок: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items/1/comment")
        );
    }

    @Test
    void updateCommentTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/items/comment/1")
                .header(xSharerUserId, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"UpdateTestComment\"}");

        Mockito.when(itemClient.updateComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentRequestDto.class)))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\":1,\"text\":\"UpdateTestComment\",\"authorName\":\"TestAuthor\",\"created\":\"2024-05-23T09:59:40\"}"));

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("{\"id\":1,\"text\":\"UpdateTestComment\",\"authorName\":\"TestAuthor\",\"created\":\"2024-05-23T09:59:40\"}")
        );
        Mockito.verify(itemClient, Mockito.times(1)).updateComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentRequestDto.class));
    }

    @Test
    void updateCommentTestNotValid() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/items/comment/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"UpdateTestComment\"}");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан заголовок: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items/comment/1")
        );
    }

    @Test
    void deleteCommentTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .delete("/items/comment/1")
                .header(xSharerUserId, 1);

        Mockito.when(itemClient.deleteComment(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(""));

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON)
        );

        Mockito.verify(itemClient, Mockito.times(1)).deleteComment(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void deleteCommentTestNotValid() throws Exception {
        var request = MockMvcRequestBuilders
                .delete("/items/comment/1");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан заголовок: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items/comment/1")
        );
    }
}