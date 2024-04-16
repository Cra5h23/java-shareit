package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

/**
 * Маппер для классов {@link ru.practicum.shareit.user.model.User} и {@link ru.practicum.shareit.user.dto.UserDto}.
 * <p>
 * Предназначен для преобразования объекта класса {@link ru.practicum.shareit.user.model.User}
 * в объект класса {@link ru.practicum.shareit.user.dto.UserDto} и наоборот.
 *
 * @author Nikolay Radzivon
 */
public class UserMapper {
    /**
     * Метод для преобразования объекта класса {@link User} в объект класса {@link UserDto}.
     *
     * @param user
     * @param id
     * @return объект класса {@link UserDto}.
     */
    public UserDto toUserDto(User user, Long id) {
        return UserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .id(id)
                .build();
    }

    /**
     * Метод для преобразования объекта класса {@link UserDto} в объект класса {@link User}.
     *
     * @param userDto
     * @return объект класса {@link User}.
     */
    public User toUser(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}
