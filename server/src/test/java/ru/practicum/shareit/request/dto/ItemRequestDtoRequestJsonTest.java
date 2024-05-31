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
class ItemRequestDtoRequestJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDtoRequest> json;

    @Test
    void testSerialize() throws IOException {
        var dto = ItemRequestDtoRequest.builder()
                .description("testDescription")
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("testDescription");
    }

    @Test
    void testToObject() throws IOException {
        var dto = json.parseObject("{\n" +
                "\"description\": \"testDescription\"\n" +
                "}\n");

        assertThat(dto).isNotNull();
        assertThat(dto.getDescription()).isEqualTo("testDescription");
        assertThat(dto.getClass()).isEqualTo(ItemRequestDtoRequest.class);
    }
}