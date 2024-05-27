package ru.practicum.shareit.booking.dto;

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
class BookerDtoJsonTest {

    @Autowired
    private JacksonTester<BookerDto> json;

    @Test
    void testSerialize() throws IOException {
        var dto = new BookerDto(1L);

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
    }

    @Test
    void testDes() throws IOException {
        var dto = json.parseObject("{\"id\": 1}");

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getClass()).isEqualTo(BookerDto.class);
    }
}