package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Интерфейс {@link ItemService}
 *
 * @author Nikolay Radzivon
 * @Date 17.04.2024
 */
public interface ItemService {
    /**
     * Метод добавления новой вещи.
     *
     * @param item   объект класса {@link Item}.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @return объект класса {@link ItemResponseDto}.
     */
    ItemResponseDto addNewItem(ItemRequestDto item, Long userId);

    /**
     * Метод обновления вещи.
     *
     * @param item   объект класса {@link Item}.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @param itemId идентификационный номер вещи.
     * @return объект класса {@link ItemResponseDto}
     */
    ItemResponseDto updateItem(ItemRequestDto item, Long userId, Long itemId);

    /**
     * Метод получения вещи по его id для определённого пользователя.
     *
     * @param itemId идентификационный номер вещи.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @return объект класса {@link ItemResponseDto}
     */
    ItemResponseDto getItemByItemId(Long itemId, Long userId);

    /**
     * Метод удаления вещи по его id для указанного пользователя.
     *
     * @param itemId идентификационный номер вещи.
     * @param userId идентификационный номер пользователя владельца вещи.
     */
    void deleteItemByItemId(Long itemId, Long userId);

    /**
     * Метод получения списка всех вещей пользователя.
     *
     * @param userId идентификационный номер пользователя владельца вещей.
     * @return {@link List} объектов {@link ItemResponseDto}.
     */
    List<ItemResponseDto> getAllItemByUser(Long userId);

    /**
     * Метод поиска вещей по тексту.
     *
     * @param text   текст по которому будет осуществлён поиск.
     * @param userId идентификационный номер пользователя у которого будет производиться поиск.
     * @return {@link List} объектов {@link ItemResponseDto}.
     */
    List<ItemResponseDto> searchItemByText(String text, Long userId);

    /**
     * Метод удаления всех вещей пользователя.
     *
     * @param userId идентификационный номер пользователя.
     */
    void deleteAllItemByUser(Long userId);
}
