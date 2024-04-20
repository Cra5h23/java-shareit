package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

/**
 * Маппер для классов {@link Item}, {@link ItemRequestDto} и {@link ItemResponseDto}.
 * <p>
 * Предназначен для преобразования объекта класса {@link Item} в объект класса {@link ItemResponseDto}
 * и объект класса {@link ItemRequestDto} в объект класса {@link Item}.
 *
 * @author Nikolay Radzivon.
 */
public class ItemMapper {
    /**
     * Метод для преобразования объекта {@link Item} в объект {@link ItemResponseDto}.
     *
     * @param item объект класса {@link Item}
     * @return объект класса {@link ItemResponseDto}.
     */
    public static ItemResponseDto toItemResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    /**
     * Метод для преобразования объекта класса {@link ItemResponseDto} в объект класса {@link Item}.
     *
     * @param itemDto объект класса {@link ItemResponseDto}
     * @param userId идентификационный номер пользователя владельца предмета.
     * @return объект класса {@link Item}.
     */
    public static Item toItem(ItemRequestDto itemDto, Long userId) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .userId(userId)
                .build();
    }
}
