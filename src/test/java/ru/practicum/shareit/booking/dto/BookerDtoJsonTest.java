package ru.practicum.shareit.booking.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
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

        Assertions.assertThat(result).hasJsonPath("$.id");
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
    }
}