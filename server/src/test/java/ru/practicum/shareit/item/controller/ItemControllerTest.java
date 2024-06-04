package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.exception.NotFoundBookingException;
import ru.practicum.shareit.exception.NotFoundCommentException;
import ru.practicum.shareit.exception.NotFoundItemException;
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemSearchParams;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Nikolay Radzivon
 * @Date 20.04.2024
 */
@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemService itemService;

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
                jsonPath("$.error").value("Не указан пользователь владелец предмета: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
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
                .content("{\"name\": \"testName1\"," +
                        "\"description\": \"testDescription1\"," +
                        "\"available\": true}");

        Mockito.when(itemService.addNewItem(Mockito.any(), Mockito.any()))
                .thenReturn(new ItemDtoResponse(1L, "testName1", "testDescription1", true, null));

        this.mockMvc.perform(request).andExpectAll(
                status().isCreated(),
                jsonPath("$.name").value("testName1"),
                jsonPath("$.id").value(1),
                jsonPath("$.description").value("testDescription1"),
                jsonPath("$.available").value(true)
        );
        Mockito.verify(itemService, Mockito.times(1)).addNewItem(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("POST /items не создаёт новую вещь если указан не существующий пользователь")
    void addNewItemTest_NotValidUserId() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/items")
                .header(xSharerUserId, 10L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testName1\"," +
                        "\"description\": \"testDescription1\"," +
                        "\"available\": true}");

        Mockito.when(itemService.addNewItem(Mockito.any(), Mockito.any()))
                .thenThrow(new NotFoundUserException("Нельзя создать новую вещь для не существующего пользователя с id 10"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами: Нельзя создать новую вещь для не существующего пользователя с id 10"),
                jsonPath("$.path").value("/items")
        );
        Mockito.verify(itemService, Mockito.times(1)).addNewItem(Mockito.any(), Mockito.any());
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

        Mockito.when(itemService.updateItem(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(new ItemDtoResponse(1L, "testName1update", "testDescription1update", false, null));

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                jsonPath("$.name").value("testName1update"),
                jsonPath("$.id").value(1),
                jsonPath("$.description").value("testDescription1update"),
                jsonPath("$.available").value(false)
        );
        Mockito.verify(itemService, Mockito.times(1)).updateItem(Mockito.any(), Mockito.any(), Mockito.any());
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
                jsonPath("$.error").value("Не указан пользователь владелец предмета: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items/1")
        );
    }


    @Test
    @DisplayName("PATCH /items/1 не обновляет вещь если у пользователя нет вещи с таким id")
    void updateItem_NotValid_ItemNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/items/10")
                .header(xSharerUserId, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testName1update\"," +
                        "\"description\": \"testDescription1update\"," +
                        "\"available\": false}");

        Mockito.when(itemService.updateItem(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenThrow(new NotFoundItemException("Нельзя обновить не существующую вещь с id 10 для пользователя с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами: Нельзя обновить не существующую вещь с id 10 для пользователя с id 1"),
                jsonPath("$.path").value("/items/10")
        );
        Mockito.verify(itemService, Mockito.times(1)).updateItem(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("PATCH /items/1 не обновляет вещь если не существует пользователя")
    void updateItem_NotValid_UserNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/items/1")
                .header(xSharerUserId, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testName1update\"," +
                        "\"description\": \"testDescription1update\"," +
                        "\"available\": false}");

        Mockito.when(itemService.updateItem(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenThrow(new NotFoundUserException("Нельзя обновить вещь c id 1 для не существующего пользователя с id 10"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами: Нельзя обновить вещь c id 1 для не существующего пользователя с id 10"),
                jsonPath("$.path").value("/items/1")
        );
        Mockito.verify(itemService, Mockito.times(1)).updateItem(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("PATCH /items/1 не обновляет вещь если пользователь не является её владельцем")
    void updateItem_NotValid_User_NotOwner() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/items/1")
                .header(xSharerUserId, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testName1update\"," +
                        "\"description\": \"testDescription1update\"," +
                        "\"available\": false}");

        Mockito.when(itemService.updateItem(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenThrow(new NotFoundItemException("Нельзя обновить вещь с id 1 пользователь с id 10 не является её владельцем"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами: Нельзя обновить вещь с id 1 пользователь с id 10 не является её владельцем"),
                jsonPath("$.path").value("/items/1")
        );
        Mockito.verify(itemService, Mockito.times(1)).updateItem(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("GET /items/1 возвращает вещь")
    void getItemById_Valid() throws Exception {
        var request = MockMvcRequestBuilders.get("/items/1")
                .header(xSharerUserId, 1);

        Mockito.when(itemService.getItemByItemId(Mockito.any(), Mockito.any()))
                .thenReturn(OwnerItemResponseDto.builder()
                        .id(1L)
                        .name("testName1")
                        .description("testDescription1")
                        .available(true)
                        .lastBooking(BookingShort.builder()
                                .id(1L)
                                .bookerId(1L)
                                .build())
                        .nextBooking(BookingShort.builder()
                                .id(3L)
                                .bookerId(4L)
                                .build())
                        .comments(List.of())
                        .build()
                );

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                jsonPath("$.name").value("testName1"),
                jsonPath("$.id").value(1),
                jsonPath("$.description").value("testDescription1"),
                jsonPath("$.available").value(true)
        );
        Mockito.verify(itemService, Mockito.times(1)).getItemByItemId(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("GET /items/1 не возвращает вещь если она не существует")
    void getItemById_NotValid_ItemNotExists() throws Exception {
        var request = MockMvcRequestBuilders.get("/items/1")
                .header(xSharerUserId, 1);

        Mockito.when(itemService.getItemByItemId(Mockito.any(), Mockito.any()))
                .thenThrow(new NotFoundItemException("Нельзя получить не существующую вещь с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами: Нельзя получить не существующую вещь с id 1"),
                jsonPath("$.path").value("/items/1")
        );
        Mockito.verify(itemService, Mockito.times(1)).getItemByItemId(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("DELETE /items/1 удаляет вещь")
    void deleteItemByItemId_Valid() throws Exception {
        var request = MockMvcRequestBuilders.delete("/items/1")
                .header(xSharerUserId, 1);

        this.mockMvc.perform(request).andExpectAll(
                status().isOk()
        );
        Mockito.verify(itemService, Mockito.times(1)).deleteItemByItemId(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("DELETE /items/1 не удаляет вещь если пользователь не существует")
    void deleteItemByItemId_NotValid_UserNotExists() throws Exception {
        var request = MockMvcRequestBuilders.delete("/items/1")
                .header(xSharerUserId, 1);

        Mockito.doThrow(new NotFoundUserException("Нельзя удалить вещь с id 1 для не существующего пользователя с id 1"))
                .when(itemService).deleteItemByItemId(Mockito.any(), Mockito.any());

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами: Нельзя удалить вещь с id 1 для не существующего пользователя с id 1"),
                jsonPath("$.path").value("/items/1")
        );
        Mockito.verify(itemService, Mockito.times(1)).deleteItemByItemId(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("DELETE /items/1 не удаляет вещь если пользователь не является её владельцем")
    void deleteItemByItemId_NotValid_UserNotOwnerItem() throws Exception {
        var request = MockMvcRequestBuilders.delete("/items/1")
                .header(xSharerUserId, 1);

        Mockito.doThrow(new NotFoundItemException("Нельзя удалить вещь с id 1 пользователь с id 1 не является её владельцем"))
                .when(itemService).deleteItemByItemId(Mockito.any(), Mockito.any());

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами: Нельзя удалить вещь с id 1 пользователь с id 1 не является её владельцем"),
                jsonPath("$.path").value("/items/1")
        );
        Mockito.verify(itemService, Mockito.times(1)).deleteItemByItemId(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("DELETE /items/1 не удаляет вещь если она не существует")
    void deleteItemByItemId_NotValid_ItemNotExists() throws Exception {
        var request = MockMvcRequestBuilders.delete("/items/1")
                .header(xSharerUserId, 1);

        Mockito.doThrow(new NotFoundItemException("Нельзя удалить не существующую вещь с id 1 для пользователя с id 1"))
                .when(itemService).deleteItemByItemId(Mockito.any(), Mockito.any());

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами: Нельзя удалить не существующую вещь с id 1 для пользователя с id 1"),
                jsonPath("$.path").value("/items/1")
        );
        Mockito.verify(itemService, Mockito.times(1)).deleteItemByItemId(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("DELETE /items/1 не удаляет вещь если не указан заголовок X-Sharer-User-Id")
    void deleteItemByItemId_X_SharerUser_Id_ExistTest() throws Exception {
        var request = MockMvcRequestBuilders
                .delete("/items/1");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан пользователь владелец предмета: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items/1")
        );
    }

    @Test
    @DisplayName("DELETE /items удаляет все предметы у указанного пользователя")
    void deleteAllItemByUser_Valid() throws Exception {
        var request = MockMvcRequestBuilders.delete("/items")
                .header(xSharerUserId, 1);

        this.mockMvc.perform(request).andExpectAll(
                status().isOk()
        );
        Mockito.verify(itemService, Mockito.times(1)).deleteAllItemByUser(Mockito.any());
    }

    @Test
    @DisplayName("DELETE /items не удаляет все предметы у указанного пользователя если он не существует")
    void deleteAllItemByUser_NotValid_User_NotExists() throws Exception {
        var request = MockMvcRequestBuilders.delete("/items")
                .header(xSharerUserId, 1);

        Mockito.doThrow(new NotFoundUserException("Нельзя удалить все вещи для не существующего пользователя с id 1"))
                .when(itemService).deleteAllItemByUser(Mockito.any());

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами: Нельзя удалить все вещи для не существующего пользователя с id 1"),
                jsonPath("$.path").value("/items")
        );
        Mockito.verify(itemService, Mockito.times(1)).deleteAllItemByUser(Mockito.any());
    }

    @Test
    @DisplayName("DELETE /items не удаляет вещи если не указан заголовок X-Sharer-User-Id")
    void deleteAllItemByUser_X_SharerUser_Id_ExistTest() throws Exception {
        var request = MockMvcRequestBuilders
                .delete("/items");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан пользователь владелец предмета: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items")
        );
    }

    @Test
    @DisplayName("GET /items возвращает список всех вещей для указанного пользователя")
    void getAllItemByUser_Valid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items")
                .param("from", "0")
                .param("size", "2")
                .header(xSharerUserId, 1);

        Mockito.when(itemService.getAllItemByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(
                        OwnerItemResponseDto.builder()
                                .id(1L)
                                .name("testName1")
                                .description("testDescription1")
                                .available(true)
                                .lastBooking(BookingShort.builder()
                                        .id(1L)
                                        .bookerId(1L)
                                        .build())
                                .nextBooking(BookingShort.builder()
                                        .id(3L)
                                        .bookerId(4L)
                                        .build())
                                .comments(List.of())
                                .build(),
                        OwnerItemResponseDto.builder()
                                .id(2L)
                                .name("testName2")
                                .description("testDescription2")
                                .available(true)
                                .lastBooking(BookingShort.builder()
                                        .id(null)
                                        .bookerId(null)
                                        .build())
                                .nextBooking(null)
                                .comments(List.of())
                                .build()
                ));

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().json("[{\"id\":1,\"name\":\"testName1\",\"description\":\"testDescription1\",\"available\":true,\"lastBooking\":{\"id\":1,\"bookerId\":1},\"nextBooking\":{\"id\":3,\"bookerId\":4},\"comments\":[]},{\"id\":2,\"name\":\"testName2\",\"description\":\"testDescription2\",\"available\":true,\"lastBooking\":{\"id\":null,\"bookerId\":null},\"nextBooking\":null,\"comments\":[]}]")
        );
        Mockito.verify(itemService, Mockito.times(1)).getAllItemByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    @DisplayName("GET /items не возвращает список всех вещей для не существующего пользователя")
    void getAllItemByUser_NotValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items")
                .param("from", "0")
                .param("size", "2")
                .header(xSharerUserId, 1);

        Mockito.when(itemService.getAllItemByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new NotFoundUserException("Нельзя получить список вещей для не существующего пользователя с id 1"));

        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами: Нельзя получить список вещей для не существующего пользователя с id 1"),
                jsonPath("$.path").value("/items")
        );
        Mockito.verify(itemService, Mockito.times(1)).getAllItemByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    @DisplayName("GET /items не возвращает список вещей если не указан заголовок X-Sharer-User-Id")
    void getAllItemByUser_X_SharerUser_Id_ExistTest() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items")
                .param("from", "0")
                .param("size", "2");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан пользователь владелец предмета: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items")
        );
    }

    @Test
    @DisplayName("GET /items/search?text=test возвращает список вещей с указанным текстом")
    void searchItemByTextValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items/search?text=test")
                .param("from", "0")
                .param("size", "2")
                .header(xSharerUserId, 1);

        Mockito.when(itemService.searchItemByText(Mockito.any(ItemSearchParams.class)))
                .thenReturn(List.of(
                        new ItemDtoResponse(1L, "testName1", "testDescription1", true, null),
                        new ItemDtoResponse(2L, "testName2", "testDescription2", true, null)
                ));

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().json("[{\"id\":1,\"name\":\"testName1\",\"description\":\"testDescription1\",\"available\":true}," +
                        "{\"id\":2,\"name\":\"testName2\",\"description\":\"testDescription2\",\"available\":true}]")
        );
        Mockito.verify(itemService, Mockito.times(1)).searchItemByText(Mockito.any(ItemSearchParams.class));
    }

    @Test
    @DisplayName("GET /items/search?text=test возвращает список вещей с указанным текстом")
    void searchItemByText_Text_Empty() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items/search?text=")
                .param("from", "0")
                .param("size", "2")
                .header(xSharerUserId, 1);

        Mockito.when(itemService.searchItemByText(Mockito.any(ItemSearchParams.class)))
                .thenReturn(List.of());

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().json("[]")
        );
        Mockito.verify(itemService, Mockito.times(1)).searchItemByText(Mockito.any(ItemSearchParams.class));
    }

    @Test
    @DisplayName("GET /items/search не ищет вещи если не указан заголовок X-Sharer-User-Id")
    void searchItemByText_X_SharerUser_Id_ExistTest() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан пользователь владелец предмета: Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items")
        );
    }

    @Test
    @DisplayName("POST /items/1/comment создаёт новый комментарий")
    void addCommentTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/items/1/comment")
                .header(xSharerUserId, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"testComment\"}");

        Mockito.when(itemService.addComment(
                        Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.any(TimeZone.class), Mockito.any(CommentRequestDto.class)))
                .thenReturn(CommentResponseDto.builder()
                        .id(1L)
                        .text("testComment")
                        .authorName("testAuthor")
                        .created(LocalDateTime.parse("2024-05-23T09:59:40"))
                        .build());

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("{\"id\":1,\"text\":\"testComment\",\"authorName\":\"testAuthor\",\"created\":\"2024-05-23T09:59:40\"}")
        );
        Mockito.verify(itemService, Mockito.times(1)).addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(TimeZone.class), Mockito.any(CommentRequestDto.class));
    }

    @Test
    @DisplayName("POST /items/1/comment не создаёт новый комментарий когда пользователь не существует")
    void addCommentTestNotValidUserNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/items/1/comment")
                .header(xSharerUserId, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"testComment\"}");

        Mockito.when(itemService.addComment(
                        Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.any(TimeZone.class), Mockito.any(CommentRequestDto.class)))
                .thenThrow(new NotFoundUserException("Нельзя оставить комментарий вещи с id 1 от не существующего пользователя с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами: Нельзя оставить комментарий вещи с id 1 от не существующего пользователя с id 1"),
                jsonPath("$.path").value("/items/1/comment")
        );
        Mockito.verify(itemService, Mockito.times(1)).addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(TimeZone.class), Mockito.any(CommentRequestDto.class));
    }

    @Test
    @DisplayName("POST /items/1/comment не создаёт новый комментарий когда вещь не существует")
    void addCommentTestNotValidItemNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/items/1/comment")
                .header(xSharerUserId, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"testComment\"}");

        Mockito.when(itemService.addComment(
                        Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.any(TimeZone.class), Mockito.any(CommentRequestDto.class)))
                .thenThrow(new NotFoundItemException("Нельзя оставить комментарий не существующей вещи id 1 от пользователя с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами: Нельзя оставить комментарий не существующей вещи id 1 от пользователя с id 1"),
                jsonPath("$.path").value("/items/1/comment")
        );
        Mockito.verify(itemService, Mockito.times(1)).addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(TimeZone.class), Mockito.any(CommentRequestDto.class));
    }

    @Test
    @DisplayName("POST /items/1/comment не создаёт новый комментарий когда вещь не существует")
    void addCommentTestNotValidBookingNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/items/1/comment")
                .header(xSharerUserId, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"testComment\"}");

        Mockito.when(itemService.addComment(
                        Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.any(TimeZone.class), Mockito.any(CommentRequestDto.class)))
                .thenThrow(new NotFoundBookingException("Нельзя оставить комментарий. Пользователь с id 1 ещё не бронировал вещь с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка работы с бронированиями: Нельзя оставить комментарий. Пользователь с id 1 ещё не бронировал вещь с id 1"),
                jsonPath("$.path").value("/items/1/comment")
        );
        Mockito.verify(itemService, Mockito.times(1)).addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(TimeZone.class), Mockito.any(CommentRequestDto.class));
    }

    @Test
    @DisplayName("PATCH /items/comment/1 обновляет комментарий")
    void updateCommentValid() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/items/comment/1")
                .header(xSharerUserId, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"UpdateTestComment\"}");

        Mockito.when(itemService.updateComment(Mockito.any(CommentRequestDto.class), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(CommentResponseDto.builder()
                        .text("UpdateTestComment")
                        .id(1L)
                        .authorName("TestAuthor")
                        .created(LocalDateTime.parse("2024-05-23T09:59:40"))
                        .build());

        this.mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().json("{\"id\":1,\"text\":\"UpdateTestComment\",\"authorName\":\"TestAuthor\",\"created\":\"2024-05-23T09:59:40\"}")
        );
        Mockito.verify(itemService, Mockito.times(1)).updateComment(Mockito.any(CommentRequestDto.class), Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    @DisplayName("PATCH /items/comment/1 не обновляет комментарий если пользователь не существует")
    void updateCommentNotValidUserNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/items/comment/1")
                .header(xSharerUserId, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"UpdateTestComment\"}");

        Mockito.when(itemService.updateComment(Mockito.any(CommentRequestDto.class), Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundUserException("Нельзя обновить комментарий c id 1 для не существующего пользователя с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами: Нельзя обновить комментарий c id 1 для не существующего пользователя с id 1"),
                jsonPath("$.path").value("/items/comment/1")
        );
        Mockito.verify(itemService, Mockito.times(1)).updateComment(Mockito.any(CommentRequestDto.class), Mockito.anyLong(), Mockito.anyLong());
    }


    @Test
    @DisplayName("PATCH /items/comment/1 не обновляет комментарий если комментарий не существует")
    void updateCommentNotValidCommentNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/items/comment/1")
                .header(xSharerUserId, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"UpdateTestComment\"}");

        Mockito.when(itemService.updateComment(Mockito.any(CommentRequestDto.class), Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundCommentException("Нельзя обновить не существующий комментарий с id 1 для пользователя с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка работы с комментариями: Нельзя обновить не существующий комментарий с id 1 для пользователя с id 1"),
                jsonPath("$.path").value("/items/comment/1")
        );
        Mockito.verify(itemService, Mockito.times(1)).updateComment(Mockito.any(CommentRequestDto.class), Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    @DisplayName("PATCH /items/comment/1 не обновляет комментарий если пользователь не является владельцем комментария")
    void updateCommentNotValidUserNotCommentOwner() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/items/comment/1")
                .header(xSharerUserId, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"text\":\"UpdateTestComment\"}");

        Mockito.when(itemService.updateComment(Mockito.any(CommentRequestDto.class), Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new NotFoundCommentException("У пользователя с id 1 нет комментария с id 1"));

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка работы с комментариями: У пользователя с id 1 нет комментария с id 1"),
                jsonPath("$.path").value("/items/comment/1")
        );
        Mockito.verify(itemService, Mockito.times(1)).updateComment(Mockito.any(CommentRequestDto.class), Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    @DisplayName("DELETE /items/comment/1 удаляет вещь")
    void deleteCommentValid() throws Exception {
        var request = MockMvcRequestBuilders
                .delete("/items/comment/1")
                .header(xSharerUserId, 1);

        this.mockMvc.perform(request).andExpectAll(status().isOk());

        Mockito.verify(itemService, Mockito.times(1)).deleteComment(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    @DisplayName("DELETE /items/comment/1 не удаляет вещь если пользователь не существует")
    void deleteCommentNotValidUserNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .delete("/items/comment/1")
                .header(xSharerUserId, 1);

        Mockito.doThrow(new NotFoundUserException("Нельзя удалить комментарий c id 1 для не существующего пользователя с id 1"))
                .when(itemService).deleteComment(Mockito.anyLong(), Mockito.anyLong());

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами: Нельзя удалить комментарий c id 1 для не существующего пользователя с id 1"),
                jsonPath("$.path").value("/items/comment/1"
                ));

        Mockito.verify(itemService, Mockito.times(1)).deleteComment(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    @DisplayName("DELETE /items/comment/1 не удаляет вещь если комментарий не существует")
    void deleteCommentNotValidCommentNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .delete("/items/comment/1")
                .header(xSharerUserId, 1);

        Mockito.doThrow(new NotFoundCommentException("Нельзя удалить не существующий комментарий с id 1 для  пользователя с id 1"))
                .when(itemService).deleteComment(Mockito.anyLong(), Mockito.anyLong());

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка работы с комментариями: Нельзя удалить не существующий комментарий с id 1 для  пользователя с id 1"),
                jsonPath("$.path").value("/items/comment/1"
                ));

        Mockito.verify(itemService, Mockito.times(1)).deleteComment(Mockito.anyLong(), Mockito.anyLong());
    }
}