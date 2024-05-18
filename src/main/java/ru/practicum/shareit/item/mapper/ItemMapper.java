package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.OwnerItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * Маппер для классов {@link Item}, {@link ItemDtoRequest} и {@link ItemDtoResponse}.
 * Предназначен для преобразования объекта класса {@link Item} в объект класса {@link ItemDtoResponse}
 * и объект класса {@link ItemDtoRequest} в объект класса {@link Item}.
 *
 * @author Nikolay Radzivon.
 */
public class ItemMapper {
    /**
     * Метод для преобразования объекта {@link Item} в объект {@link ItemDtoResponse}.
     *
     * @param item объект класса {@link Item}
     * @return объект класса {@link ItemDtoResponse}.
     */
    public static ItemDtoResponse toItemResponseDto(Item item) {
        return ItemDtoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    /**
     * Метод для преобразования объекта класса {@link ItemDtoResponse} в объект класса {@link Item}.
     *
     * @param itemDto объект класса {@link ItemDtoResponse}
     * @param user    Пользователь владелец вещи.
     * @return объект класса {@link Item}.
     */
    public static Item toItem(ItemDtoRequest itemDto, User user) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .build();
    }

    public static OwnerItemResponseDto toOwnerItemResponseDto(Item item,
                                                              BookingShort lastBooking,
                                                              BookingShort nextBooking,
                                                              List<CommentResponseDto> comments) {
        return OwnerItemResponseDto.builder()
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}
