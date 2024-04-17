package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

/**
 * Интерфейс {@link ItemService}
 *
 * @author Nikolay Radzivon
 * @Date 17.04.2024
 */
public interface ItemService {
    /**
     * Метод добавления нового предмета.
     *
     * @param item   объект класса {@link Item}.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @return объект класса {@link ItemDto}.
     */
    ItemDto addNewItem(Item item, Long userId);

    /**
     * Метод обновления предмета.
     *
     * @param item   объект класса {@link Item}.
     * @param userId идентификационный номер пользователя владельца предмета.
     * @param itemId идентификационный номер предмета.
     * @return объект класса {@link ItemDto}
     */
    ItemDto updateItem(Item item, Long userId, Long itemId);

}
