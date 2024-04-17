package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

/**
 * Маппер для классов {@link ru.practicum.shareit.item.model.Item} и {@link ru.practicum.shareit.item.dto.ItemDto}.
 * <p>
 * Предназначен для преобразования объекта класса {@link Item} в объект класса {@link ItemDto} и наоборот.
 *
 * @author Nikolay Radzivon.
 */
@Component
public class ItemMapper {
    /**
     * Метод для преобразования объекта {@link Item} в объект {@link ItemDto}.
     *
     * @param item
     * @return объект класса {@link ItemDto}.
     */
    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    /**
     * Метод для преобразования объекта класса {@link ItemDto} в объект класса {@link Item}.
     *
     * @param itemDto
     * @param userId
     * @return объект класса {@link Item}.
     */
    public Item toItem(ItemDto itemDto, Long userId) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .userId(userId)
                .build();
    }

}
