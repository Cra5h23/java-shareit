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
class CommentRequestDtoJsonTest {
    @Autowired
    private JacksonTester<CommentRequestDto> json;

    @Test
    void testSerialize() throws IOException {
        var dto = new CommentRequestDto("TestText");

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("TestText");
    }

    @Test
    void testToObject() throws IOException {
        var dto = json.parseObject("{\"text\": \"testText\"}");

        assertThat(dto).isNotNull();
        assertThat(dto.getText()).isEqualTo("testText");
        assertThat(dto.getClass()).isEqualTo(CommentRequestDto.class);
    }
}