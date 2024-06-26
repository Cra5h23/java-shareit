package ru.practicum.shareit.request.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Nikolay Radzivon
 * @Date 31.05.2024
 */
@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItemRequest(Long userId, ItemRequestDtoRequest request) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> getUserRequests(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId, Integer from, Integer size) {
        if (from == null || size == null) return ResponseEntity.ok(List.of());

        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getRequest(Long requestId, Long userId) {
        return get("/" + requestId, userId);
    }
}
