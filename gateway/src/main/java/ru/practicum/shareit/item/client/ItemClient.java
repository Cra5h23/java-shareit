package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Nikolay Radzivon
 * @Date 31.05.2024
 */
@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(Long userId, ItemDtoRequest item) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> updateItem(Long itemId, Long userId, ItemDtoRequest item) {
        return patch("/" + itemId, userId, item);
    }

    public ResponseEntity<Object> getItem(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> deleteItem(Long itemId, Long userId) {
        return delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> deleteAllUserItems(Long userId) {
        return delete("", userId);
    }

    public ResponseEntity<Object> getUserItems(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        return get("", userId, parameters);
    }

    public ResponseEntity<Object> searchItem(Long userId, String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(List.of());
        }

        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );

        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(Long itemId, Long userId, CommentRequestDto text) {
        return post(String.format("/%d/comment", itemId), userId, text);
    }

    public ResponseEntity<Object> updateComment(Long commentId, Long userId, CommentRequestDto comment) {
        return patch("/comment/" + commentId, userId, comment);
    }

    public ResponseEntity<Object> deleteComment(Long commentId, Long userId) {
        return delete("/comment/" + commentId, userId);
    }
}
