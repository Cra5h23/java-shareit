package ru.practicum.shareit.item.repository.impl;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Nikolay Radzivon
 * @Date 17.04.2024
 */
public class InMemoryItemRepository implements ItemRepository {
    /**
     * Метод добавления нового предмета в репозиторий.
     *
     * @param item   объект класса {@link Item}
     * @param userId идентификационный номер пользователя владельца предмета.
     * @return объект класса {@link ItemDto} созданный предмет.
     */
    @Override
    public ItemDto save(Item item, Long userId) {
        return null;
    }

    /**
     * Метод обновления предмета.
     *
     * @param item   объект класса {@link Item} данные для обновления.
     * @param userId идентификационный номер пользователя владельца предмета.
     * @param itemId идентификационный номер предмета.
     * @return объект класса {@link ItemDto} обновлённый предмет.
     */
    @Override
    public ItemDto update(Item item, Long userId, Long itemId) {
        return null;
    }

    /**
     * Метод получения предмета по id.
     *
     * @param itemId идентификационный номер предмета.
     * @param userId идентификационный номер пользователя владельца предмета.
     * @return объект класса {@link ItemDto} запрошенный предмет.
     */
    @Override
    public Optional<ItemDto> findById(Long itemId, Long userId) {
        return Optional.empty();
    }

    /**
     * Метод удаления предмета по id.
     *
     * @param itemId идентификационный номер предмета.
     * @param userId идентификационный номер пользователя владельца предмета.
     */
    @Override
    public void deleteById(Long itemId, Long userId) {

    }

    /**
     * Метод получения списка всех предметов для указанного пользователя.
     *
     * @param userId идентификатор пользователя владельца предметов.
     * @return {@link List} объектов {@link ItemDto} список предметов для указанного пользователя.
     */
    @Override
    public List<ItemDto> findAllById(Long userId) {
        return List.of();
    }

    /**
     * Метод поиска предметов для указанного пользователя по указанному тексту.
     *
     * @param text   текс поиска.
     * @param userId идентификатор пользователя владельца предметов.
     * @return {@link List} объектов {@link ItemDto} список предметов для указанного пользователя удовлетворяющих параметрам поиска.
     */
    @Override
    public List<ItemDto> search(String text, Long userId) {
        return List.of();
    }

    /**
     * Метод удаления всех предметов для указанного пользователя.
     *
     * @param userId идентификатор пользователя владельца предметов.
     */
    @Override
    public void deleteAll(Long userId) {

    }
}
