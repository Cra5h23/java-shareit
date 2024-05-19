package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemFromItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreated;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemResponse;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

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

    public static ItemRequestDtoCreated toItemRequestDtoCreated(ItemRequest request) {
        return ItemRequestDtoCreated.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated().toLocalDateTime())
                .build();
    }

    public static ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest request) {

        return ItemRequestDtoResponse.builder()
                .created(request.getCreated().toLocalDateTime())
                .description(request.getDescription())
                .response(toListItemFromItemRequest(request.getResponses()))
                .build();
    }

    private static List<ItemFromItemRequest> toListItemFromItemRequest(List<ItemResponse> responses) {
        return responses.stream()
                .map(i -> ItemFromItemRequest.builder()
                        .requestId(i.getRequest().getId())
                        .name(i.getItem().getName())
                        .id(i.getItem().getId())
                        .available(i.getItem().getAvailable())
                        .description(i.getItem().getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
