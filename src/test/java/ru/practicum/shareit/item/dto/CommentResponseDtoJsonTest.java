package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
@JsonTest
class CommentResponseDtoJsonTest {
    @Autowired
    private JacksonTester<CommentResponseDto> json;

    @Test
    void testSerialize() throws IOException {
        var dto = new CommentResponseDto(1L, "testText", "TestAuthor", "2023-06-10T18:23:00");

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("testText");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("TestAuthor");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-06-10T18:23:00");
    }

    @Test
    void testToObject() throws IOException {
        var dto = json.parseObject("{\n" +
                "  \"id\": 1,\n" +
                "  \"text\": \"testText\",\n" +
                "  \"authorName\": \"TestAuthor\",\n" +
                "  \"created\": \"2023-06-10T18:23:00\"\n" +
                "}");

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getText()).isEqualTo("testText");
        assertThat(dto.getAuthorName()).isEqualTo("TestAuthor");
        assertThat(dto.getCreated()).isEqualTo("2023-06-10T18:23:00");
        assertThat(dto.getClass()).isEqualTo(CommentResponseDto.class);
    }
}