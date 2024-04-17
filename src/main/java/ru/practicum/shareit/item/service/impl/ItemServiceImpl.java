package ru.practicum.shareit.item.service.impl;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * @author Nikolay Radzivon
 * @Date 17.04.2024
 */
public class ItemServiceImpl implements ItemService {
    /**
     * Метод добавления нового предмета.
     *
     * @param item   объект класса {@link Item}.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @return объект класса {@link ItemDto}.
     */
    @Override
    public ItemDto addNewItem(Item item, Long userId) {
        return null;
    }

    /**
     * Метод обновления предмета.
     *
     * @param item   объект класса {@link Item}.
     * @param userId идентификационный номер пользователя владельца предмета.
     * @param itemId идентификационный номер предмета.
     * @return объект класса {@link ItemDto}
     */
    @Override
    public ItemDto updateItem(Item item, Long userId, Long itemId) {
        return null;
    }

    /**
     * Метод получения предмета по его id для определённого пользователя.
     *
     * @param itemId идентификационный номер предмета.
     * @param userId идентификационный номер пользователя владельца предмета.
     * @return объект класса {@link ItemDto}
     */
    @Override
    public ItemDto getItemByItemId(Long itemId, Long userId) {
        return null;
    }

    /**
     * Метод удаления предмета по его id для указанного пользователя.
     *
     * @param itemId идентификационный номер предмета.
     * @param userId идентификационный номер пользователя владельца предмета.
     */
    @Override
    public void deleteItemByItemId(Long itemId, Long userId) {

    }

    /**
     * Метод получения списка всех предметов пользователя.
     *
     * @param userId идентификационный номер пользователя владельца предметов.
     * @return {@link List} объектов {@link ItemDto}.
     */
    @Override
    public List<ItemDto> getAllItemByUser(Long userId) {
        return List.of();
    }

    /**
     * Метод поиска предметов по тексту для пользователя.
     *
     * @param text   текст по которому будет осуществлён поиск.
     * @param userId идентификационный номер пользователя у которого будет производится поиск.
     * @return {@link List} объектов {@link ItemDto}.
     */
    @Override
    public List<ItemDto> searchItemByText(String text, Long userId) {
        return List.of();
    }

    /**
     * Метод удаления всех предметов пользователя.
     *
     * @param userId идентификационный номер пользователя.
     */
    @Override
    public void deleteAllItemByUser(Long userId) {

    }
}
