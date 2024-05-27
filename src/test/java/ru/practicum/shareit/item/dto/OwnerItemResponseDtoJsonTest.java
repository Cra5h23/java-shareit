package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
@JsonTest
class OwnerItemResponseDtoJsonTest {
    @Autowired
    private JacksonTester<OwnerItemResponseDto> json;

    @Test
    void testSerialize() throws IOException {
        var dto = OwnerItemResponseDto.builder()
                .id(32L)
                .name("TestName")
                .available(true)
                .lastBooking(BookingShort.builder().id(23L).bookerId(12L).build())
                .nextBooking(BookingShort.builder().id(21L).bookerId(11L).build())
                .description("testDescription")
                .comments(List.of(CommentResponseDto.builder()
                        .id(24L)
                        .created("2024-05-06T:13:11:25")
                        .authorName("TestAuthor")
                        .text("testText")
                        .build()))
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(32);
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("TestName");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).hasJsonPath("$.lastBooking.id");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(23);
        assertThat(result).hasJsonPath("$.lastBooking.bookerId");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(12);

        assertThat(result).hasJsonPath("$.nextBooking.id");
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(21);
        assertThat(result).hasJsonPath("$.lastBooking.bookerId");
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(11);
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("testDescription");

        assertThat(result).hasJsonPath("$.comments.[0].id");
        assertThat(result).extractingJsonPathNumberValue("$.comments.[0].id").isEqualTo(24);

        assertThat(result).hasJsonPath("$.comments.[0].created");
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].created").isEqualTo("2024-05-06T:13:11:25");

        assertThat(result).hasJsonPath("$.comments.[0].authorName");
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].authorName").isEqualTo("TestAuthor");
        assertThat(result).hasJsonPath("$.comments.[0].text");
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].text").isEqualTo("testText");
    }

    @Test
    void testToObject() throws IOException {
        var dto = json.parseObject("{\n" +
                "  \"id\": 32,\n" +
                "  " +
                "\"available\": true,\n" +
                "  \"description\": \"testDescription\",\n" +
                "  \"name\": \"testName\",\n" +
                "  \"lastBooking\": {\n" +
                "    \"id\": 23,\n" +
                "    \"bookerId\": 12\n" +
                "  },\n" +
                "  \"nextBooking\": {\n" +
                "    \"id\": 21,\n" +
                "    \"bookerId\": 11\n" +
                "  },\n" +
                "  \"comments\": [\n" +
                "    {\n" +
                "      \"id\": 24,\n" +
                "      \"created\": \"2024-05-06T:13:11:25\",\n" +
                "      \"authorName\": \"TestAuthor\",\n" +
                "      \"text\": \"testText\"\n" +
                "    }\n" +
                "  ]\n" +
                "}");

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(32);
        assertThat(dto.getAvailable()).isEqualTo(true);
        assertThat(dto.getDescription()).isEqualTo("testDescription");
        assertThat(dto.getName()).isEqualTo("testName");
        assertThat(dto.getNextBooking().getId()).isEqualTo(21);
        assertThat(dto.getNextBooking().getBookerId()).isEqualTo(11);
        assertThat(dto.getLastBooking().getId()).isEqualTo(23);
        assertThat(dto.getLastBooking().getBookerId()).isEqualTo(12);
        assertThat(dto.getComments().size()).isEqualTo(1);
        assertThat(dto.getComments().get(0).getCreated()).isEqualTo("2024-05-06T:13:11:25");
        assertThat(dto.getComments().get(0).getText()).isEqualTo("testText");
        assertThat(dto.getComments().get(0).getAuthorName()).isEqualTo("TestAuthor");
        assertThat(dto.getComments().get(0).getId()).isEqualTo(24);
        assertThat(dto.getClass()).isEqualTo(OwnerItemResponseDto.class);
    }
}