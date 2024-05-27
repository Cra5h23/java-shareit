package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class UserMapperTest {

    @Test
    void toUserResponseDtoTest() {
        var test = UserMapper.toUserResponseDto(User.builder()
                .id(23L)
                .name("testName")
                .email("testUser@email.com")
                .build());

        assertNotNull(test);
        assertEquals(23, test.getId());
        assertEquals("testName", test.getName());
        assertEquals("testUser@email.com", test.getEmail());
    }

    @Test
    void toUserTest() {
        var test = UserMapper.toUser(UserRequestDto.builder()
                .name("testName")
                .email("testUser@email.com")
                .build());

        assertNotNull(test);
        assertEquals("testName", test.getName());
        assertEquals("testUser@email.com", test.getEmail());
    }
}