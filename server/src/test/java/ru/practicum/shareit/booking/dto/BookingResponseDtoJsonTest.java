package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
@JsonTest
class BookingResponseDtoJsonTest {
    @Autowired
    private JacksonTester<BookingResponseDto> json;

    @Test
    void testSerialize() throws IOException {
        var booker = BookerDto.builder().id(1L).build();
        var item = ItemBookingDto.builder().id(2L).name("TestItem").build();
        var dto = new BookingResponseDto(1L, LocalDateTime.parse("2023-06-10T15:23:00"), LocalDateTime.parse("2023-06-10T18:23:00"), BookingStatus.APPROVED, booker, item);

        var result = json.write(dto);

        assertThat(result).isNotNull();
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-06-10T15:23:00");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-06-10T18:23:00");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).hasJsonPath("$.booker.id");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).hasJsonPath("$.item.id");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        assertThat(result).hasJsonPath("$.item.name");
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("TestItem");
    }

    @Test
    void testToObject() throws IOException {
        var dto = json.parseObject("{\"id\": 1, \"start\": \"2023-06-10T15:23:00\", \"end\": \"2023-06-10T18:23:00\", \"status\": \"APPROVED\", \"booker\": {\"id\": 1}, \"item\": {\"id\": 2, \"name\": \"testName\"}}");

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getItem().getId()).isEqualTo(2);
        assertThat(dto.getItem().getName()).isEqualTo("testName");
        assertThat(dto.getBooker().getId()).isEqualTo(1);
        assertThat(dto.getEnd()).isEqualTo("2023-06-10T18:23:00");
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(dto.getStart()).isEqualTo("2023-06-10T15:23:00");
    }
}