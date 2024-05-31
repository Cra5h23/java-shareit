package ru.practicum.shareit.item.dto;

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
class ItemDtoResponseJsonTest {
    @Autowired
    private JacksonTester<ItemDtoResponse> json;

    @Test
    void testSerialize() throws IOException {
        var dto = ItemDtoResponse.builder()
                .id(32L)
                .name("TestName")
                .available(true)
                .requestId(1L)
                .description("testDescription")
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(32);
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("TestName");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("testDescription");
    }

    @Test
    void testToObject() throws IOException {
        var dto = json.parseObject("{\n" +
                "  \"id\": 32,\n" +
                "  \"requestId\": 1,\n" +
                "  \"available\": true,\n" +
                "  \"description\": \"testDescription\",\n" +
                "  \"name\": \"testName\"\n" +
                "}");

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(32);
        assertThat(dto.getRequestId()).isEqualTo(1);
        assertThat(dto.getAvailable()).isEqualTo(true);
        assertThat(dto.getDescription()).isEqualTo("testDescription");
        assertThat(dto.getName()).isEqualTo("testName");
        assertThat(dto.getClass()).isEqualTo(ItemDtoResponse.class);
    }
}