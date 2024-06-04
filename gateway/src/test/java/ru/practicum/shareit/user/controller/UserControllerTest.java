package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserSort;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Nikolay Radzivon
 * @Date 03.06.2024
 */
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserClient userClient;

    @Test
    void addNewUserTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testUser1\",\n" +
                        "\"email\": \"testEmail1@test.com\"}");

        Mockito.when(userClient.addUser(Mockito.any(UserRequestDto.class)))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1,\"name\": \"testUser1\",\"email\": \"testEmail1@test.com\"}"));

        mockMvc.perform(request).andExpectAll(
                status().isCreated(),
                jsonPath("$.id").value(1),
                jsonPath("$.name").value("testUser1"),
                jsonPath("$.email").value("testEmail1@test.com")
        );
        Mockito.verify(userClient, Mockito.times(1))
                .addUser(Mockito.any(UserRequestDto.class));
    }

    @Test
    void addNewUserTestNotValidEmptyName() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"testEmail1@test.com\"}");

        mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных пользователя: Поле name не должно быть пустым"),
                jsonPath("$.path").value("/users")
        );
    }

    @Test
    void addNewUserTestNotValidEmptyEmail() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testUser1\"}");

        mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных пользователя: Поле email не должно быть пустым"),
                jsonPath("$.path").value("/users")
        );
    }

    @Test
    @DisplayName("POST /users не создаёт нового пользователя если поле email не соответствует формату электронной почты")
    void addNewUserTestNotValidFailEmail() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testUser1\",\n" +
                        "\"email\": \"testEmail1.com\"}");

        mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных пользователя: Поле email должно иметь формат адреса электронной почты"),
                jsonPath("$.path").value("/users")
        );
    }

    @Test
    void updateUserTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testUser1update\",\n" +
                        "\"email\": \"testEmail1update@test.com\"}");

        Mockito.when(userClient.updateUser(Mockito.anyLong(), Mockito.any(UserRequestDto.class)))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1,\"name\": \"testUser1update\",\"email\": \"testEmail1update@test.com\"}"));

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                jsonPath("$.id").value(1),
                jsonPath("$.name").value("testUser1update"),
                jsonPath("$.email").value("testEmail1update@test.com")
        );
        Mockito.verify(userClient, Mockito.times(1)).updateUser(Mockito.anyLong(), Mockito.any(UserRequestDto.class));
    }

    @Test
    void updateUserTestNotValidFailEmail() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testUser1update\",\n" +
                        "\"email\": \"testEmail1updatetest.com\"}");

        mockMvc.perform(request).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(400),
                jsonPath("$.error").value("Ошибка ввода данных пользователя: Поле email должно иметь формат адреса электронной почты"),
                jsonPath("$.path").value("/users/1")
        );
    }

    @Test
    void getUserTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/users/1");

        Mockito.when(userClient.getUser(Mockito.anyLong()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .body("{\"id\": 1,\"name\": \"testUser1\",\"email\": \"testEmail1@test.com\"}"));

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                jsonPath("$.id").value(1),
                jsonPath("$.name").value("testUser1"),
                jsonPath("$.email").value("testEmail1@test.com")
        );
        Mockito.verify(userClient, Mockito.times(1)).getUser(Mockito.anyLong());
    }

    @Test
    void deleteUserTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .delete("/users/1");

        Mockito.when(userClient.deleteUser(Mockito.anyLong()))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(""));

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON)
        );
        Mockito.verify(userClient, Mockito.times(1)).deleteUser(Mockito.anyLong());
    }

    @Test
    void getAllUsersTestValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/users/?");

        Mockito.when(userClient.getUsers(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(UserSort.class)))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[{\"id\": 1,\"name\": \"testUser1\",\"email\": \"testEmail1@test.com\"},{\"id\": 2,\"name\": \"testUser2\",\"email\": \"testEmail2@test.com\"}]"));

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().json("[{\"id\":1,\"name\":\"testUser1\",\"email\":\"testEmail1@test.com\"},{\"id\":2,\"name\":\"testUser2\",\"email\":\"testEmail2@test.com\"}]")
        );
        Mockito.verify(userClient, Mockito.times(1)).getUsers(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(UserSort.class));

    }
}