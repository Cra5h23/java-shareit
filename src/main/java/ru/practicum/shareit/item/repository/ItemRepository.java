package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс {@link ItemRepository}
 *
 * @author Nikolay Radzivon
 * @Date 17.04.2024
 */
public interface ItemRepository {
    /**
     * Метод добавления нового предмета в репозиторий.
     *
     * @param item   объект класса {@link Item}
     * @param userId идентификационный номер пользователя владельца предмета.
     * @return объект класса {@link ItemDto} созданный предмет.
     */
    ItemDto save(Item item, Long userId);

    /**
     * Метод обновления предмета.
     *
     * @param item   объект класса {@link Item} данные для обновления.
     * @param userId идентификационный номер пользователя владельца предмета.
     * @param itemId идентификационный номер предмета.
     * @return объект класса {@link ItemDto} обновлённый предмет.
     */
    ItemDto update(Item item, Long userId, Long itemId);

    /**
     * Метод получения предмета по id.
     *
     * @param itemId идентификационный номер предмета.
     * @param userId идентификационный номер пользователя владельца предмета.
     * @return объект класса {@link ItemDto} запрошенный предмет.
     */
    Optional<ItemDto> findById(Long itemId, Long userId);

    /**
     * Метод удаления предмета по id.
     *
     * @param itemId идентификационный номер предмета.
     * @param userId идентификационный номер пользователя владельца предмета.
     */
    void deleteById(Long itemId, Long userId);

    /**
     * Метод получения списка всех предметов для указанного пользователя.
     *
     * @param userId идентификатор пользователя владельца предметов.
     * @return {@link List} объектов {@link ItemDto} список предметов для указанного пользователя.
     */
    List<ItemDto> findAllById(Long userId);

    /**
     * Метод поиска предметов для указанного пользователя по указанному тексту.
     *
     * @param text   текс поиска.
     * @param userId идентификатор пользователя владельца предметов.
     * @return {@link List} объектов {@link ItemDto} список предметов для указанного пользователя удовлетворяющих параметрам поиска.
     */
    List<ItemDto> search(String text, Long userId);

    /**
     * Метод удаления всех предметов для указанного пользователя.
     *
     * @param userId идентификатор пользователя владельца предметов.
     */
    void deleteAll(Long userId);
}

