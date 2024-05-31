package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
@JsonTest
class ItemRequestDtoResponseJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDtoResponse> json;

    @Test
    void testSerialize() throws IOException {
        var dto = ItemRequestDtoResponse.builder()
                .id(12L)
                .created(LocalDateTime.of(2024, 3, 12, 13, 25, 25))
                .description("testDescription")
                .items(List.of(
                        ItemFromItemRequest.builder()
                                .id(23L)
                                .requestId(12L)
                                .name("testItemName")
                                .description("testItemDescription")
                                .available(true)
                                .build()
                ))
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(12);
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-03-12T13:25:25");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("testDescription");
        assertThat(result).hasJsonPath("$.items.[0].id");
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].id").isEqualTo(23);
        assertThat(result).hasJsonPath("$.items.[0].requestId");
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].requestId").isEqualTo(12);
        assertThat(result).hasJsonPath("$.items.[0].name");
        assertThat(result).extractingJsonPathStringValue("$.items.[0].name").isEqualTo("testItemName");
        assertThat(result).hasJsonPath("$.items.[0].available");
        assertThat(result).extractingJsonPathBooleanValue("$.items.[0].available").isEqualTo(true);
        assertThat(result).hasJsonPath("$.items.[0].description");
        assertThat(result).extractingJsonPathStringValue("$.items.[0].description").isEqualTo("testItemDescription");
    }

    @Test
    void testToObject() throws IOException {
        var dto = json.parseObject("{\n" +
                "  \"id\": 1,\n" +
                "  \"description\": \"testDescription\",\n" +
                "  \"created\": \"2024-03-12T12:13:20\",\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"id\": 23,\n" +
                "      \"requestId\": 12,\n" +
                "      \"name\": \"testItemName\",\n" +
                "      \"description\": \"testItemDescription\",\n" +
                "      \"available\": true\n" +
                "    }\n" +
                "  ]\n" +
                "}");

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getCreated()).isEqualTo("2024-03-12T12:13:20");
        assertThat(dto.getDescription()).isEqualTo("testDescription");

        assertThat(dto.getItems().get(0).getId()).isEqualTo(23);
        assertThat(dto.getItems().get(0).getRequestId()).isEqualTo(12);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("testItemName");
        assertThat(dto.getItems().get(0).getAvailable()).isEqualTo(true);
        assertThat(dto.getItems().get(0).getDescription()).isEqualTo("testItemDescription");

        assertThat(dto.getClass()).isEqualTo(ItemRequestDtoResponse.class);
    }
}