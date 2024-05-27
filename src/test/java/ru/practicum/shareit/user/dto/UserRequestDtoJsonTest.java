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
class UserRequestDtoJsonTest {
    @Autowired
    private JacksonTester<UserRequestDto> json;

    @Test
    void testSerialize() throws IOException {
        var dto = UserRequestDto.builder()
                .name("testUser")
                .email("testUser@email.com")
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("testUser");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("testUser@email.com");
    }

    @Test
    void testToObject() throws IOException {
        var dto = json.parseObject("{\n" +
                "  \"name\": \"testUser\",\n" +
                "  \"email\": \"testUser@email.com\"\n" +
                "}");

        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo("testUser@email.com");
        assertThat(dto.getName()).isEqualTo("testUser");
        assertThat(dto.getClass()).isEqualTo(UserRequestDto.class);
    }
}