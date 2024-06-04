package ru.practicum.shareit.booking.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Nikolay Radzivon
 * @Date 03.06.2024
 */
@RestClientTest(BookingClient.class)
class BookingClientTest {
    @Autowired
    BookingClient bookingClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void getBookingsTest() {
        this.server.expect(requestTo("http://localhost:9090/bookings?state=ALL&from=0&size=2"))
                .andRespond(withSuccess(
                        "[{\"id\": 2," +
                                "\"start\":\"2024-06-05T08:01:34\"," +
                                "\"end\":\"2024-06-06T08:01:34\"," +
                                "\"status\": \"APPROVED\"," +
                                "\"booker\":{" +
                                "\"id\": 1" +
                                "}," +
                                " \"item\":{" +
                                "\"id\": 2," +
                                " \"name\": \"Отвертка\"}},{\n" +
                                "  \"id\": 5,\n" +
                                "  \"start\": \"2024-06-04T08:01:42\",\n" +
                                "  \"end\": \"2024-06-05T08:01:39\",\n" +
                                "  \"status\": \"REJECTED\",\n" +
                                "  \"booker\": {\n" +
                                "    \"id\": 1\n" +
                                "  },\n" +
                                "  \"item\": {\n" +
                                "    \"id\": 3,\n" +
                                "    \"name\": \"Клей Момент\"\n" +
                                "  }\n" +
                                "}]", MediaType.APPLICATION_JSON));

        var response = bookingClient.getBookings(1, BookingState.ALL, 0, 2);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("[{id=2, start=2024-06-05T08:01:34, end=2024-06-06T08:01:34, status=APPROVED, booker={id=1}, item={id=2, name=Отвертка}}, {id=5, start=2024-06-04T08:01:42, end=2024-06-05T08:01:39, status=REJECTED, booker={id=1}, item={id=3, name=Клей Момент}}]",
                response.getBody().toString());
    }

    @Test
    void bookItemTest() {
        this.server.expect(requestTo("http://localhost:9090/bookings"))
                .andRespond(withSuccess(
                        "{\"id\": 9," +
                                "\"start\": \"2024-06-04T08:26:48\"," +
                                "\"end\": \"2024-06-04T08:26:49\"," +
                                " \"status\": \"WAITING\"," +
                                " \"booker\": {" +
                                "\"id\": 1" +
                                "}," +
                                " \"item\": {" +
                                "\"id\": 2," +
                                " \"name\": \"Отвертка\"" +
                                "}" +
                                "}", MediaType.APPLICATION_JSON)
                );
        var response = bookingClient.bookItem(1, BookingRequestDto.builder()
                .itemId(2L)
                .start(LocalDateTime.parse("2024-06-04T08:26:48"))
                .end(LocalDateTime.parse("2024-06-04T08:26:49"))
                .build());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(response.getHeaders());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("{id=9, start=2024-06-04T08:26:48, end=2024-06-04T08:26:49, status=WAITING, booker={id=1}, item={id=2, name=Отвертка}}",
                response.getBody().toString());
    }

    @Test
    void getBookingTest() {
        this.server.expect(requestTo("http://localhost:9090/bookings/2"))
                .andRespond(withSuccess("{" +
                        "\"id\": 2," +
                        " \"start\": \"2024-06-05T08:01:34\"," +
                        " \"end\": \"2024-06-06T08:01:34\"," +
                        " \"status\": \"APPROVED\"," +
                        " \"booker\": {" +
                        "\"id\": 1" +
                        "}," +
                        " \"item\": {" +
                        "\"id\": 2," +
                        " \"name\": \"Отвертка\"" +
                        "}" +
                        "}", MediaType.APPLICATION_JSON));

        var response = bookingClient.getBooking(4, 2L);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(response.getHeaders());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("{id=2, start=2024-06-05T08:01:34, end=2024-06-06T08:01:34, status=APPROVED, booker={id=1}, item={id=2, name=Отвертка}}",
                response.getBody().toString());
    }

    @Test
    void getBookingsOwnerTest() {
        this.server.expect(requestTo("http://localhost:9090/bookings/owner?state=ALL&from=0&size=2"))
                .andRespond(withSuccess("[{\n" +
                        "  \"id\": 2,\n" +
                        "  \"start\": \"2024-06-05T08:01:34\",\n" +
                        "  \"end\": \"2024-06-06T08:01:34\",\n" +
                        "  \"status\": \"APPROVED\",\n" +
                        "  \"booker\": {\n" +
                        "    \"id\": 1\n" +
                        "  },\n" +
                        "  \"item\": {\n" +
                        "    \"id\": 2,\n" +
                        "    \"name\": \"Отвертка\"\n" +
                        "  }\n" +
                        "},\n" +
                        "{\n" +
                        "\"id\": 4,\n" +
                        "\"start\": \"2024-06-04T09:01:35\",\n" +
                        "\"end\": \"2024-06-04T10:01:35\",\n" +
                        "\"status\": \"APPROVED\",\n" +
                        "\"booker\": {\n" +
                        "\"id\": 5\n" +
                        "},\n" +
                        "\"item\": {\n" +
                        "\"id\": 2,\n" +
                        "\"name\": \"Отвертка\"\n" +
                        "}\n" +
                        "}]", MediaType.APPLICATION_JSON));

        var response = bookingClient.getBookingsOwner(4, BookingState.ALL, 0, 2);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(response.getHeaders());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("[{id=2, start=2024-06-05T08:01:34, end=2024-06-06T08:01:34, status=APPROVED, booker={id=1}, item={id=2, name=Отвертка}}, {id=4, start=2024-06-04T09:01:35, end=2024-06-04T10:01:35, status=APPROVED, booker={id=5}, item={id=2, name=Отвертка}}]",
                response.getBody().toString());

    }

    @Test
    void confirmationBookingTest() {
        this.server.expect(requestTo("http://localhost:9090/bookings/1?approved=false"))
                .andRespond(withSuccess("{\n" +
                        "  \"id\": 1,\n" +
                        "  \"start\": \"2024-06-04T08:01:37\",\n" +
                        "  \"end\": \"2024-06-04T08:01:38\",\n" +
                        "  \"status\": \"REJECTED\",\n" +
                        "  \"booker\": {\n" +
                        "    \"id\": 1\n" +
                        "  },\n" +
                        "  \"item\": {\n" +
                        "    \"id\": 2,\n" +
                        "    \"name\": \"Отвертка\"\n" +
                        "  }\n" +
                        "}", MediaType.APPLICATION_JSON));

        var response = bookingClient.confirmationBooking(4, false, 1);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNotNull(response.getHeaders());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("{id=1, start=2024-06-04T08:01:37, end=2024-06-04T08:01:38, status=REJECTED, booker={id=1}, item={id=2, name=Отвертка}}",
                response.getBody().toString());
    }
}