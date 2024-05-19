package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDtoCreated;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * @author Nikolay Radzivon
 * @Date 19.05.2024
 */
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDtoRequest request, User user, TimeZone timeZone) {
        return ItemRequest.builder()
                .description(request.getDescription())
                .requestor(user)
                .created(LocalDateTime.now().atZone(timeZone.toZoneId()))
                .build();
    }

    public static ItemRequestDtoCreated toItemRequestDtoCreated (ItemRequest request) {
        return ItemRequestDtoCreated.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated().toLocalDateTime())
                .build();
    }
}
