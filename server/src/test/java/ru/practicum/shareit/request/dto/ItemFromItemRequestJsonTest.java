package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
@JsonTest
class ItemFromItemRequestJsonTest {
    @Autowired
    private JacksonTester<ItemFromItemRequest> json;

    @Test
    void testSerialize() throws IOException {
        var dto = ItemFromItemRequest.builder()
                .id(12L)
                .name("testName")
                .requestId(23L)
                .available(true)
                .description("testDescription")
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(12);

        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("testName");

        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(23);

        assertThat(result).hasJsonPath("$.available");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);

        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("testDescription");
    }

    @Test
    void testToObject() throws IOException {
        var dto = json.parseObject("{\n" +
                "  \"id\": 1,\n" +
                "  \"description\": \"testDescription\",\n" +
                "  \"name\": \"testName\",\n" +
                "  \"requestId\": 23,\n" +
                "  \"available\": true\n" +
                "}");

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getRequestId()).isEqualTo(23);
        assertThat(dto.getDescription()).isEqualTo("testDescription");
        assertThat(dto.getAvailable()).isEqualTo(true);
        assertThat(dto.getName()).isEqualTo("testName");
        assertThat(dto.getClass()).isEqualTo(ItemFromItemRequest.class);
    }
}