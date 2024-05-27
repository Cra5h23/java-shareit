package ru.practicum.shareit.user.dto;

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
class UserResponseDtoJsonTest {
    @Autowired
    private JacksonTester<UserResponseDto> json;

    @Test
    void testSerialize() throws IOException {
        var dto = UserResponseDto.builder()
                .name("testUser")
                .email("testUser@email.com")
                .id(1L)
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("testUser");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("testUser@email.com");
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }

    @Test
    void testToObject() throws IOException {
        var dto = json.parseObject("{\n" +
                "  \"name\": \"testUser\",\n" +
                "  \"email\": \"testUser@email.com\",\n" +
                "  \"id\": 1\n" +
                "}");

        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo("testUser@email.com");
        assertThat(dto.getName()).isEqualTo("testUser");
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getClass()).isEqualTo(UserResponseDto.class);
    }
}