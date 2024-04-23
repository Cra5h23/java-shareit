package ru.practicum.shareit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Класс создания форматированного ответа в случае ошибки.
 *
 * @author Nikolay Radzivon
 * @Date 19.04.2024
 */
public class ErrorResponse {
    /**
     * Метод для создания форматированного ответа в случае ошибки.
     *
     * @param webRequest
     * @param status
     * @param message
     * @return
     */
    public static ResponseEntity<Map<String, Object>> makeErrorResponse(
            WebRequest webRequest, HttpStatus status, String message) {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", message);

        String string = Objects.requireNonNull(webRequest.getAttribute(
                "org.springframework.boot.web.servlet.error.DefaultErrorAttributes.ERROR", 0)).toString();
        String substring = string.substring(string.lastIndexOf(":") + 2);

        response.put("message", substring);
        response.put("path", Objects.requireNonNull(webRequest.getAttribute(
                "org.springframework.web.util.ServletRequestPathUtils.PATH", 0)).toString());

        return ResponseEntity.status(status).body(response);
    }
}