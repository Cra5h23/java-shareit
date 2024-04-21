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
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.exception.ItemRepositoryException;
import ru.practicum.shareit.item.exception.ItemServiceException;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

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
                jsonPath("$.error").value("Не указан пользователь владелец предмета"),
                jsonPath("$.message").value("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
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
                .thenReturn(new ItemResponseDto(1L, "testName1", "testDescription1", true));

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
                jsonPath("$.error").value("Ошибка ввода данных предмета"),
                jsonPath("$.message").value("Название предмета не может быть пустым"),
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
                jsonPath("$.error").value("Ошибка ввода данных предмета"),
                jsonPath("$.message").value("Возможность аренды должна быть указана"),
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
                jsonPath("$.error").value("Ошибка ввода данных предмета"),
                jsonPath("$.message").value("Описание предмета не может быть пустым"),
                jsonPath("$.path").value("/items")
        );
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
                .thenThrow(new ItemServiceException("Нельзя создать новую вещь для не существующего пользователя с id 10"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами"),
                jsonPath("$.message").value("Нельзя создать новую вещь для не существующего пользователя с id 10"),
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
                .thenReturn(new ItemResponseDto(1L, "testName1update", "testDescription1update", false));

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
                jsonPath("$.error").value("Не указан пользователь владелец предмета"),
                jsonPath("$.message").value("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
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
                .thenThrow(new ItemRepositoryException("У пользователя с id 1 нет вещи для редактирования с id 10"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами"),
                jsonPath("$.message").value("У пользователя с id 1 нет вещи для редактирования с id 10"),
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
                .thenThrow(new ItemServiceException("Нельзя обновить вещь для не существующего пользователя с id 10"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами"),
                jsonPath("$.message").value("Нельзя обновить вещь для не существующего пользователя с id 10"),
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
                .thenReturn(new ItemResponseDto(1L, "testName1", "testDescription1", true));

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
                .thenThrow(new ItemServiceException("Вещь с id 1 не существует"));

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами"),
                jsonPath("$.message").value("Вещь с id 1 не существует"),
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

        Mockito.doThrow(new ItemServiceException("Нельзя удалить вещь для не существующего пользователя с id 1"))
                .when(itemService).deleteItemByItemId(Mockito.any(), Mockito.any());


        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами"),
                jsonPath("$.message").value("Нельзя удалить вещь для не существующего пользователя с id 1"),
                jsonPath("$.path").value("/items/1")
        );
        Mockito.verify(itemService, Mockito.times(1)).deleteItemByItemId(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("DELETE /items/1 не удаляет вещь если у пользователя её нет")
    void deleteItemByItemId_NotValid_ItemNotExists() throws Exception {
        var request = MockMvcRequestBuilders.delete("/items/1")
                .header(xSharerUserId, 1);

        Mockito.doThrow(new ItemServiceException("Вещь с id 1 не существует у пользователя с id 1"))
                .when(itemService).deleteItemByItemId(Mockito.any(), Mockito.any());


        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами"),
                jsonPath("$.message").value("Вещь с id 1 не существует у пользователя с id 1"),
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
                jsonPath("$.error").value("Не указан пользователь владелец предмета"),
                jsonPath("$.message").value("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
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

        Mockito.doThrow(new ItemServiceException("Нельзя удалить вещи для не существующего пользователя с id 1"))
                .when(itemService).deleteAllItemByUser(Mockito.any());

        this.mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами"),
                jsonPath("$.message").value("Нельзя удалить вещи для не существующего пользователя с id 1"),
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
                jsonPath("$.error").value("Не указан пользователь владелец предмета"),
                jsonPath("$.message").value("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items")
        );
    }

    @Test
    @DisplayName("GET /items возвращает список всех вещей для указанного пользователя")
    void getAllItemByUser_Valid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items")
                .header(xSharerUserId, 1);

        Mockito.when(itemService.getAllItemByUser(Mockito.any()))
                .thenReturn(List.of(
                        new ItemResponseDto(1L, "testName1", "testDescription1", true),
                        new ItemResponseDto(2L, "testName2", "testDescription2", false)
                ));

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().json("[{\"id\":1,\"name\":\"testName1\",\"description\":\"testDescription1\",\"available\":true}," +
                        "{\"id\":2,\"name\":\"testName2\",\"description\":\"testDescription2\",\"available\":false}]")
        );
        Mockito.verify(itemService, Mockito.times(1)).getAllItemByUser(Mockito.any());
    }

    @Test
    @DisplayName("GET /items не возвращает список всех вещей для не существующего пользователя")
    void getAllItemByUser_NotValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items")
                .header(xSharerUserId, 1);

        Mockito.when(itemService.getAllItemByUser(Mockito.any()))
                .thenThrow(new ItemServiceException("Нельзя получить список вещей для не существующего пользователя с id 1"));

        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с предметами"),
                jsonPath("$.message").value("Нельзя получить список вещей для не существующего пользователя с id 1"),
                jsonPath("$.path").value("/items")
        );
        Mockito.verify(itemService, Mockito.times(1)).getAllItemByUser(Mockito.any());
    }

    @Test
    @DisplayName("GET /items не возвращает список вещей если не указан заголовок X-Sharer-User-Id")
    void getAllItemByUser_X_SharerUser_Id_ExistTest() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items");

        this.mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Не указан пользователь владелец предмета"),
                jsonPath("$.message").value("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items")
        );
    }

    @Test
    @DisplayName("/GET /items/search?text=test возвращает список вещей с указанным текстом")
    void searchItemByTextValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items/search?text=test")
                .header(xSharerUserId, 1);

        Mockito.when(itemService.searchItemByText(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(
                        new ItemResponseDto(1L, "testName1", "testDescription1", true),
                        new ItemResponseDto(2L, "testName2", "testDescription2", true)
                ));

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().json("[{\"id\":1,\"name\":\"testName1\",\"description\":\"testDescription1\",\"available\":true}," +
                        "{\"id\":2,\"name\":\"testName2\",\"description\":\"testDescription2\",\"available\":true}]")
        );
        Mockito.verify(itemService, Mockito.times(1)).searchItemByText(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("/GET /items/search?text=test возвращает список вещей с указанным текстом")
    void searchItemByText_Text_Empty() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/items/search?text=")
                .header(xSharerUserId, 1);

        Mockito.when(itemService.searchItemByText(Mockito.any(), Mockito.any()))
                .thenReturn(List.of());

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().json("[]")
        );
        Mockito.verify(itemService, Mockito.times(1)).searchItemByText(Mockito.any(), Mockito.any());
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
                jsonPath("$.error").value("Не указан пользователь владелец предмета"),
                jsonPath("$.message").value("Required request header 'X-Sharer-User-Id' for method parameter type Long is not present"),
                jsonPath("$.path").value("/items")
        );
    }
}