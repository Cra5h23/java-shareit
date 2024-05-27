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
class ItemBookingDtoJsonTest {
    @Autowired
    JacksonTester<ItemBookingDto> json;

    @Test
    void testSerialize() throws IOException {
        var dto = new ItemBookingDto(1L, "TestName");

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("TestName");
    }

    @Test
    void testDes() throws IOException {
        var dto = json.parseObject("{\"id\": 1, \"name\": \"TestName\"}");

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("TestName");
        assertThat(dto.getClass()).isEqualTo(ItemBookingDto.class);
    }
}