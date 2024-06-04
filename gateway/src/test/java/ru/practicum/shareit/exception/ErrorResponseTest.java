package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * @author Nikolay Radzivon
 * @Date 03.06.2024
 */
class ErrorResponseTest {
    @Autowired
    final WebRequest webRequest = Mockito.mock(WebRequest.class);

    @Test
    void makeErrorResponseTest() {

        Mockito.when(webRequest.getAttribute(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(": errorMessage");
        ResponseEntity<Map<String, Object>> message = ErrorResponse.makeErrorResponse(webRequest, HttpStatus.OK, "message");

        HttpStatus statusCode = message.getStatusCode();
        Map<String, Object> body = message.getBody();

        System.out.println(body);
        Assertions.assertNotNull(body);
        Assertions.assertEquals(200,statusCode.value());
        Assertions.assertEquals(200, body.get("status"));
        Assertions.assertEquals("messageerrorMessage", body.get("error"));
        Assertions.assertEquals(": errorMessage", body.get("path"));
    }
}