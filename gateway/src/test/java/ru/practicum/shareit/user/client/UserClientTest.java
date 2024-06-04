package ru.practicum.shareit.user.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserSort;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Nikolay Radzivon
 * @Date 04.06.2024
 */
@RestClientTest(UserClient.class)
class UserClientTest {
    @Autowired
    UserClient userClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void addUserTest() {
        this.server.expect(requestTo("http://localhost:9090/users"))
                .andRespond(withSuccess("{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"user\",\n" +
                        "  \"email\": \"user@user.com\"\n" +
                        "}", MediaType.APPLICATION_JSON));

        var response = userClient.addUser(UserRequestDto.builder()
                .name("user")
                .email("user@user.com")
                .build());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("{id=1, name=user, email=user@user.com}", response.getBody().toString());
    }

    @Test
    void updateUserTest() {
        this.server.expect(requestTo("http://localhost:9090/users/1"))
                .andRespond(withSuccess("{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"UpdateUser\",\n" +
                        "  \"email\": \"UpdateUser@user.com\"\n" +
                        "}", MediaType.APPLICATION_JSON));

        var response = userClient.updateUser(1L, UserRequestDto.builder()
                .name("UpdateUser")
                .email("UpdateUser@user.com")
                .build());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("{id=1, name=UpdateUser, email=UpdateUser@user.com}", response.getBody().toString());
    }

    @Test
    void getUserTest() {
        this.server.expect(requestTo("http://localhost:9090/users/1"))
                .andRespond(withSuccess("{\n" +
                        "\"id\": 1,\n" +
                        "\"name\": \"user\",\n" +
                        "  \"email\": \"user@user.com\"\n" +
                        "}", MediaType.APPLICATION_JSON));

        var response = userClient.getUser(1L);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("{id=1, name=user, email=user@user.com}", response.getBody().toString());
    }

    @Test
    void deleteUserTest() {
        this.server.expect(requestTo("http://localhost:9090/users/1"))
                .andRespond(withSuccess());

        var response = userClient.deleteUser(1L);

        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getHeaders().getContentType());
    }

    @Test
    void getUsersTest() {
        this.server.expect(requestTo("http://localhost:9090/users"))
                .andRespond(withSuccess("[\n" +
                        "  {\n" +
                        "    \"id\": 4,\n" +
                        "    \"name\": \"user\",\n" +
                        "    \"email\": \"user@user.com\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"id\": 1,\n" +
                        "    \"name\": \"updateName\",\n" +
                        "    \"email\": \"updateName@user.com\"\n" +
                        "  }\n" +
                        "]", MediaType.APPLICATION_JSON));

        var response = userClient.getUsers(1, 2, UserSort.NONE);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("[{id=4, name=user, email=user@user.com}, {id=1, name=updateName, email=updateName@user.com}]", response.getBody().toString());
    }
}