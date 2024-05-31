package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
@JsonTest
class ItemRequestDtoCreatedJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDtoCreated> json;

    @Test
    void testSerialize() throws IOException {
        var dto = ItemRequestDtoCreated.builder()
                .id(12L)
                .created(LocalDateTime.of(2024, 3, 12, 13, 25, 25))
                .description("testDescription")
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(12);

        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-03-12T13:25:25");

        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("testDescription");
    }

    @Test
    void testToObject() throws IOException {
        var dto = json.parseObject("{\n" +
                "  \"id\": 1,\n" +
                "  \"description\": \"testDescription\",\n" +
                "  \"created\": \"2024-03-12T12:13:20\"\n" +
                "}");

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getCreated()).isEqualTo("2024-03-12T12:13:20");
        assertThat(dto.getDescription()).isEqualTo("testDescription");
        assertThat(dto.getClass()).isEqualTo(ItemRequestDtoCreated.class);
    }
}