package ru.practicum.shareit.request.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Nikolay Radzivon
 * @Date 04.06.2024
 */
@RestClientTest(ItemRequestClient.class)
class ItemRequestClientTest {
    @Autowired
    ItemRequestClient itemRequestClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void addItemRequestTest() {
        this.server.expect(requestTo("http://localhost:9090/requests"))
                .andRespond(withSuccess("{\n" +
                        "  \"id\": 1,\n" +
                        "  \"description\": \"Хотел бы воспользоваться щёткой для обуви\",\n" +
                        "  \"created\": \"2024-06-04T10:46:52.0753847\"\n" +
                        "}", MediaType.APPLICATION_JSON));

        var response = itemRequestClient.addItemRequest(1L, ItemRequestDtoRequest.builder()
                .description("Хотел бы воспользоваться щёткой для обуви")
                .build());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("{id=1, description=Хотел бы воспользоваться щёткой для обуви, created=2024-06-04T10:46:52.0753847}",
                response.getBody().toString());
    }

    @Test
    void getUserRequestsTest() {
        this.server.expect(requestTo("http://localhost:9090/requests"))
                .andRespond(withSuccess(
                        "[\n" +
                                "  {\n" +
                                "    \"id\": 1,\n" +
                                "    \"description\": \"Хотел бы воспользоваться щёткой для обуви\",\n" +
                                "    \"created\": \"2024-06-04T10:46:52.075385\",\n" +
                                "    \"items\": []\n" +
                                "  }\n" +
                                "]",
                        MediaType.APPLICATION_JSON));

        var response = itemRequestClient.getUserRequests(1L);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("[{id=1, description=Хотел бы воспользоваться щёткой для обуви, created=2024-06-04T10:46:52.075385, items=[]}]",
                response.getBody().toString());
    }

    @Test
    void getAllRequestsTest() {
        this.server.expect(requestTo("http://localhost:9090/requests/all?from=0&size=1"))
                .andRespond(withSuccess(
                        "[\n" +
                                "  {\n" +
                                "    \"id\": 1,\n" +
                                "    \"description\": \"Хотел бы воспользоваться щёткой для обуви\",\n" +
                                "    \"created\": \"2024-06-04T10:46:52.075385\",\n" +
                                "    \"items\": []\n" +
                                "  }\n" +
                                "]",
                        MediaType.APPLICATION_JSON));

        var response = itemRequestClient.getAllRequests(1L, 0, 1);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("[{id=1, description=Хотел бы воспользоваться щёткой для обуви, created=2024-06-04T10:46:52.075385, items=[]}]",
                response.getBody().toString());
    }

    @Test
    void getRequestTest() {
        this.server.expect(requestTo("http://localhost:9090/requests/1"))
                .andRespond(withSuccess(
                        "{\n" +
                                "  \"id\": 1,\n" +
                                "  \"description\": \"Хотел бы воспользоваться щёткой для обуви\",\n" +
                                "  \"created\": \"2024-06-04T10:46:52.075385\",\n" +
                                "  \"items\": []\n" +
                                "}",
                        MediaType.APPLICATION_JSON));

        var response = itemRequestClient.getRequest(1L, 1L);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("{id=1, description=Хотел бы воспользоваться щёткой для обуви, created=2024-06-04T10:46:52.075385, items=[]}",
                response.getBody().toString());
    }
}