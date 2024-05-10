package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.OwnerItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * Маппер для классов {@link Item}, {@link ItemRequestDto} и {@link ItemResponseDto}.
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
     * @param user    Пользователь владелец вещи.
     * @return объект класса {@link Item}.
     */
    public static Item toItem(ItemRequestDto itemDto, User user) {
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
