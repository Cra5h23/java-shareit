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
class BookingShortJsonTest {
    @Autowired
    private JacksonTester<BookingShort> json;

    @Test
    void testSerialize() throws IOException {
        var dto = new BookingShort(1L, 2L);

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);

        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
    }

    @Test
    void testToObject() throws IOException {
        var dto = json.parseObject("{\"id\": 1, \"bookerId\": 2}");

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getBookerId()).isEqualTo(2);
        assertThat(dto.getClass()).isEqualTo(BookingShort.class);
    }
}