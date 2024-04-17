package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
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

    /**
     * Метод получения предмета по его id для определённого пользователя.
     *
     * @param itemId идентификационный номер предмета.
     * @param userId идентификационный номер пользователя владельца предмета.
     * @return объект класса {@link ItemDto}
     */
    ItemDto getItemByItemId(Long itemId, Long userId);

    /**
     * Метод удаления предмета по его id для указанного пользователя.
     *
     * @param itemId идентификационный номер предмета.
     * @param userId идентификационный номер пользователя владельца предмета.
     */
    void deleteItemByItemId(Long itemId, Long userId);

    /**
     * Метод получения списка всех предметов пользователя.
     *
     * @param userId идентификационный номер пользователя владельца предметов.
     * @return {@link List} объектов {@link ItemDto}.
     */
    List<ItemDto> getAllItemByUser(Long userId);

    /**
     * Метод поиска предметов по тексту для пользователя.
     *
     * @param text   текст по которому будет осуществлён поиск.
     * @param userId идентификационный номер пользователя у которого будет производится поиск.
     * @return {@link List} объектов {@link ItemDto}.
     */
    List<ItemDto> searchItemByText(String text, Long userId);

}
