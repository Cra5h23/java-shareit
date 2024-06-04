package ru.practicum.shareit.item.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Nikolay Radzivon
 * @Date 04.06.2024
 */
@RestClientTest(ItemClient.class)
class ItemClientTest {
    @Autowired
    ItemClient itemClient;

    @Autowired
    MockRestServiceServer server;

    @Test
    void addItemTest() {
        this.server.expect(requestTo("http://localhost:9090/items"))
                .andRespond(withSuccess("{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"Дрель\",\n" +
                        "  \"description\": \"Простая дрель\",\n" +
                        "  \"available\": true,\n" +
                        "  \"requestId\": null\n" +
                        "}", MediaType.APPLICATION_JSON));

        var response = itemClient.addItem(1L, ItemDtoRequest.builder()
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("{id=1, name=Дрель, description=Простая дрель, available=true, requestId=null}", response.getBody().toString());
    }

    @Test
    void updateItem() {
        this.server.expect(requestTo("http://localhost:9090/items/1"))
                .andRespond(withSuccess("{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"Дрель+\",\n" +
                        "  \"description\": \"Аккумуляторная дрель\",\n" +
                        "  \"available\": true,\n" +
                        "  \"requestId\": null\n" +
                        "}", MediaType.APPLICATION_JSON));

        var response = itemClient.updateItem(1L, 1L, ItemDtoRequest.builder()
                .name("Дрель+")
                .description("Аккумуляторная дрель")
                .available(false)
                .build());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("{id=1, name=Дрель+, description=Аккумуляторная дрель, available=true, requestId=null}", response.getBody().toString());
    }

    @Test
    void getItemTest() {
        this.server.expect(requestTo("http://localhost:9090/items/1"))
                .andRespond(withSuccess("{\n" +
                        "  \"id\": 1,\n" +
                        "\"name\": \"Дрель\",\n" +
                        "\"description\": \"Простая дрель\",\n" +
                        "\"available\": true,\n" +
                        "\"lastBooking\": null,\n" +
                        "\"nextBooking\": null,\n" +
                        "\"comments\": []\n" +
                        "}", MediaType.APPLICATION_JSON));

        var response = itemClient.getItem(1L, 1L);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("{id=1, name=Дрель, description=Простая дрель, available=true, lastBooking=null, nextBooking=null, comments=[]}", response.getBody().toString());

    }

    @Test
    void deleteItemTest() {
        this.server.expect(requestTo("http://localhost:9090/items/1"))
                .andRespond(withSuccess());

        var response = itemClient.deleteItem(1L, 1L);

        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteAllUserItemsTest() {
        this.server.expect(requestTo("http://localhost:9090/items"))
                .andRespond(withSuccess());

        var response = itemClient.deleteAllUserItems(1L);

        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getUserItemsTest() {
        this.server.expect(requestTo("http://localhost:9090/items"))
                .andRespond(withSuccess("[\n" +
                        "  {\n" +
                        "    \"id\": 2,\n" +
                        "    \"name\": \"Отвертка\",\n" +
                        "    \"description\": \"Аккумуляторная отвертка\",\n" +
                        "    \"available\": true,\n" +
                        "    \"lastBooking\": null,\n" +
                        "    \"nextBooking\": null,\n" +
                        "    \"comments\": []\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"id\": 3,\n" +
                        "    \"name\": \"Клей Момент\",\n" +
                        "    \"description\": \"Тюбик суперклея марки Момент\",\n" +
                        "    \"available\": true,\n" +
                        "    \"lastBooking\": null,\n" +
                        "    \"nextBooking\": null,\n" +
                        "    \"comments\": []\n" +
                        "  }\n" +
                        "]", MediaType.APPLICATION_JSON));

        var response = itemClient.getUserItems(1L, 0, 2);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("[{id=2, name=Отвертка, description=Аккумуляторная отвертка, available=true, lastBooking=null, nextBooking=null, comments=[]}, {id=3, name=Клей Момент, description=Тюбик суперклея марки Момент, available=true, lastBooking=null, nextBooking=null, comments=[]}]", response.getBody().toString());
    }

    @Test
    void searchItemTest() {
        this.server.expect(requestTo("http://localhost:9090/items/search?text=%D0%B4%D1%80%D0%B5%D0%BB%D1%8C&from=0&size=2"))
                .andRespond(withSuccess(
                        "[\n" +
                                "  {\n" +
                                "    \"id\": 1,\n" +
                                "    \"name\": \"Дрель\",\n" +
                                "    \"description\": \"Простая дрель\",\n" +
                                "    \"available\": true,\n" +
                                "    \"requestId\": null\n" +
                                "  }\n" +
                                "]",
                        MediaType.APPLICATION_JSON));

        var response = itemClient.searchItem(1L, "дрель", 0, 2);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("[{id=1, name=Дрель, description=Простая дрель, available=true, requestId=null}]", response.getBody().toString());
    }

    @Test
    void addCommentTest() {
        this.server.expect(requestTo("http://localhost:9090/items/1/comment"))
                .andRespond(withSuccess(
                        "{\n" +
                                "  \"id\": 1,\n" +
                                "  \"text\": \"Add comment from user1\",\n" +
                                "  \"authorName\": \"updateName\",\n" +
                                "  \"created\": \"2024-06-04T10:24:19.8872443\"\n" +
                                "}",
                        MediaType.APPLICATION_JSON));

        var response = itemClient.addComment(1L, 2L, CommentRequestDto.builder()
                .text("Add comment from user1")
                .build());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("{id=1, text=Add comment from user1, authorName=updateName, created=2024-06-04T10:24:19.8872443}", response.getBody().toString());
    }

    @Test
    void updateCommentTest() {
        this.server.expect(requestTo("http://localhost:9090/items/comment/1"))
                .andRespond(withSuccess(
                        "{\n" +
                                "  \"id\": 1,\n" +
                                "  \"text\": \"updateComment\",\n" +
                                "  \"authorName\": \"updateName\",\n" +
                                "  \"created\": \"2024-06-04T10:24:19.8872443\"\n" +
                                "}",
                        MediaType.APPLICATION_JSON));

        var response = itemClient.updateComment(1L, 2L, CommentRequestDto.builder()
                .text("updateComment")
                .build());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("{id=1, text=updateComment, authorName=updateName, created=2024-06-04T10:24:19.8872443}", response.getBody().toString());
    }

    @Test
    void deleteCommentTest() {
        this.server.expect(requestTo("http://localhost:9090/items/comment/1"))
                .andRespond(withSuccess());

        var response = itemClient.deleteComment(1L, 2L);

        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}