package ru.practicum.shareit.booking.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
@JsonTest
class BookingRequestDtoJsonTest {
    @Autowired
    private JacksonTester<BookingRequestDto> json;

    @Test
    void testSerialize() throws IOException {
        var dto = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 6, 10, 15, 23))
                .end(LocalDateTime.of(2023, 6, 10, 18, 23))
                .build();

        var result = json.write(dto);

        Assertions.assertThat(result).hasJsonPath("$.itemId");
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(dto.getItemId().intValue());
        Assertions.assertThat(result).hasJsonPath("$.start");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-06-10T15:23:00");
        Assertions.assertThat(result).hasJsonPath("$.end");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-06-10T18:23:00");
    }

    @Test
    void testDes() throws IOException {
        var dto = json.parseObject("{\"itemId\": 1, \"start\": \"2023-06-10T18:23:00\", \"end\": \"2023-06-11T18:23:00\"}");

        Assertions.assertThat(dto).isNotNull();
        Assertions.assertThat(dto.getItemId()).isEqualTo(1);
        Assertions.assertThat(dto.getStart()).isEqualTo(LocalDateTime.parse("2023-06-10T18:23:00"));
        Assertions.assertThat(dto.getEnd()).isEqualTo(LocalDateTime.parse("2023-06-11T18:23:00"));
    }
}