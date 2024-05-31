package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

/**
 * Маппер для классов {@link ru.practicum.shareit.user.model.User}, {@link UserRequestDto} и {@link UserResponseDto}.
 * <p>
 * Предназначен для преобразования объекта класса {@link ru.practicum.shareit.user.model.User}
 * в объект класса {@link UserResponseDto}, объекта класса {@link UserRequestDto} в объект класса {@link User}.
 *
 * @author Nikolay Radzivon
 */
public class UserMapper {
    /**
     * Метод для преобразования объекта класса {@link User} в объект класса {@link UserResponseDto}.
     *
     * @param user объект класса {@link User}
     * @return объект класса {@link UserResponseDto}.
     */
    public static UserResponseDto toUserResponseDto(User user) {
        return UserResponseDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .id(user.getId())
                .build();
    }

    /**
     * Метод для преобразования объекта класса {@link UserRequestDto} в объект класса {@link User}.
     *
     * @param userRequestDto объект класса {@link UserRequestDto}
     * @return объект класса {@link User}.
     */
    public static User toUser(UserRequestDto userRequestDto) {
        return User.builder()
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .build();
    }
}
