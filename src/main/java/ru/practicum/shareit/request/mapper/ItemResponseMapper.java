package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemResponse;

/**
 * @author Nikolay Radzivon
 * @Date 19.05.2024
 */
public class ItemResponseMapper {

    public static ItemResponse toItemResponse(Item item, Long requestId) {
        return ItemResponse.builder()
                .item(item)
                .request(ItemRequest.builder()
                        .id(requestId)
                        .build())
                .build();
    }
}
