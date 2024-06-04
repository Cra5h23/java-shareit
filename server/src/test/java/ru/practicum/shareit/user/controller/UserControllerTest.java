package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.exeption.UserRepositoryException;
import ru.practicum.shareit.user.model.UserSort;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Nikolay Radzivon
 * @Date 21.04.2024
 */
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Test
    @DisplayName("POST /users создаёт нового пользователя")
    void addNewUser_Valid() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testUser1\",\n" +
                        "\"email\": \"testEmail1@test.com\"}");

        Mockito.when(userService.addNewUser(Mockito.any(UserRequestDto.class))).thenReturn(
                new UserResponseDto(1L, "testUser1", "testEmail1@test.com"));

        mockMvc.perform(request).andExpectAll(
                status().isCreated(),
                jsonPath("$.id").value(1),
                jsonPath("$.name").value("testUser1"),
                jsonPath("$.email").value("testEmail1@test.com")
        );
        Mockito.verify(userService, Mockito.times(1)).addNewUser(Mockito.any(UserRequestDto.class));
    }

    @Test
    @DisplayName("POST /users создаёт нового пользователя")
    void addNewUser_NotValid_DuplicateEmail() throws Exception {
        var request = MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testUser1\",\n" +
                        "\"email\": \"testEmail1@test.com\"}");

        Mockito.when(userService.addNewUser(Mockito.any(UserRequestDto.class)))
                .thenThrow(new UserRepositoryException("Нельзя добавить нового пользователя, Пользователь с email testEmail1@test.com уже существует"));

        mockMvc.perform(request).andExpectAll(
                status().isConflict(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(409),
                jsonPath("$.error").value("Ошибка работы с пользователями: Нельзя добавить нового пользователя, Пользователь с email testEmail1@test.com уже существует"),
                jsonPath("$.path").value("/users")
        );
        Mockito.verify(userService, Mockito.times(1)).addNewUser(Mockito.any(UserRequestDto.class));
    }

    @Test
    @DisplayName("PATCH /users/1 обновляет пользователя")
    void updateUser_Valid() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testUser1update\",\n" +
                        "\"email\": \"testEmail1update@test.com\"}");

        Mockito.when(userService.updateUser(Mockito.any(UserRequestDto.class), Mockito.anyLong()))
                .thenReturn(new UserResponseDto(1L, "testUser1update", "testEmail1update@test.com"));

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                jsonPath("$.id").value(1),
                jsonPath("$.name").value("testUser1update"),
                jsonPath("$.email").value("testEmail1update@test.com")
        );
        Mockito.verify(userService, Mockito.times(1)).updateUser(Mockito.any(UserRequestDto.class), Mockito.anyLong());
    }

    @Test
    @DisplayName("PATCH /users/1 не обновляет не существующего пользователя")
    void updateUser_NotValid_UserNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testUser1update\",\n" +
                        "\"email\": \"testEmail1update@test.com\"}");

        Mockito.when(userService.updateUser(Mockito.any(UserRequestDto.class), Mockito.anyLong()))
                .thenThrow(new NotFoundUserException("Нельзя обновить не существующего пользователя с id 1"));

        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с пользователями: Нельзя обновить не существующего пользователя с id 1"),
                jsonPath("$.path").value("/users/1")
        );
        Mockito.verify(userService, Mockito.times(1)).updateUser(Mockito.any(UserRequestDto.class), Mockito.anyLong());
    }

    @Test
    @DisplayName("PATCH /users/ не обновляет пользователя когда такой email уже существует")
    void updateUser_NotValid_DuplicateEmail() throws Exception {
        var request = MockMvcRequestBuilders
                .patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"testUser1\",\n" +
                        "\"email\": \"testEmail1@test.com\"}");

        Mockito.when(userService.updateUser(Mockito.any(UserRequestDto.class), Mockito.anyLong()))
                .thenThrow(new UserRepositoryException("Нельзя обновить пользователя с id 1, пользователь с email testEmail1@test.com уже существует"));

        mockMvc.perform(request).andExpectAll(
                status().isConflict(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(409),
                jsonPath("$.error").value("Ошибка работы с пользователями: Нельзя обновить пользователя с id 1, пользователь с email testEmail1@test.com уже существует"),
                jsonPath("$.path").value("/users/1")
        );
        Mockito.verify(userService, Mockito.times(1)).updateUser(Mockito.any(UserRequestDto.class), Mockito.anyLong());
    }

    @Test
    @DisplayName("GET /users/1 возвращает пользователя")
    void getUser_Valid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/users/1");

        Mockito.when(userService.getUser(Mockito.anyLong()))
                .thenReturn(new UserResponseDto(1L, "testUser1", "testEmail1@test.com"));

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                jsonPath("$.id").value(1),
                jsonPath("$.name").value("testUser1"),
                jsonPath("$.email").value("testEmail1@test.com")
        );
        Mockito.verify(userService, Mockito.times(1)).getUser(Mockito.anyLong());
    }

    @Test
    @DisplayName("GET /users/1 не возвращает пользователя")
    void getUser_NotValid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/users/1");

        Mockito.when(userService.getUser(Mockito.anyLong()))
                .thenThrow(new NotFoundUserException("Нельзя получить не существующего пользователя с id 1"));

        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с пользователями: Нельзя получить не существующего пользователя с id 1"),
                jsonPath("$.path").value("/users/1")
        );
        Mockito.verify(userService, Mockito.times(1)).getUser(Mockito.anyLong());
    }

    @Test
    @DisplayName("DELETE /users/1 удаляет пользователя")
    void deleteUser_Valid() throws Exception {
        var request = MockMvcRequestBuilders
                .delete("/users/1");

        Mockito.doNothing().when(userService).deleteUser(Mockito.anyLong());

        mockMvc.perform(request).andExpectAll(
                status().isOk()
        );
        Mockito.verify(userService, Mockito.times(1)).deleteUser(Mockito.anyLong());
    }

    @Test
    @DisplayName("DELETE /users/1 не удаляет не существующего пользователя")
    void deleteUser_NotValid_UserNotExists() throws Exception {
        var request = MockMvcRequestBuilders
                .delete("/users/1");

        Mockito.doThrow(new NotFoundUserException("Нельзя удалить не существующего пользователя с id 1"))
                .when(userService).deleteUser(Mockito.anyLong());

        mockMvc.perform(request).andExpectAll(
                status().isNotFound(),
                jsonPath("$.timestamp").exists(),
                jsonPath("$.status").value(404),
                jsonPath("$.error").value("Ошибка работы с пользователями: Нельзя удалить не существующего пользователя с id 1"),
                jsonPath("$.path").value("/users/1")
        );
        Mockito.verify(userService, Mockito.times(1)).deleteUser(Mockito.anyLong());
    }

    @Test
    @DisplayName("GET /users возвращает список всех пользователей")
    void getAllUsers_Valid() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/users/?");

        Mockito.when(userService.getAllUsers(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(UserSort.class)))
                .thenReturn(List.of(new UserResponseDto(1L, "testUser1", "testEmail1@test.com"),
                        new UserResponseDto(2L, "testUser2", "testEmail2@test.com")));

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().json("[{\"id\":1,\"name\":\"testUser1\",\"email\":\"testEmail1@test.com\"},{\"id\":2,\"name\":\"testUser2\",\"email\":\"testEmail2@test.com\"}]")
        );
        Mockito.verify(userService, Mockito.times(1)).getAllUsers(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(UserSort.class));
    }

    @Test
    @DisplayName("GET /users возвращает пустой список если пользователей не существует")
    void getAllUsers_Valid_EmptyList() throws Exception {
        var request = MockMvcRequestBuilders
                .get("/users");

        Mockito.when(userService.getAllUsers(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(UserSort.class)))
                .thenReturn(List.of());

        mockMvc.perform(request).andExpectAll(
                status().isOk(),
                content().json("[]")
        );
        Mockito.verify(userService, Mockito.times(1)).getAllUsers(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(UserSort.class));
    }
}