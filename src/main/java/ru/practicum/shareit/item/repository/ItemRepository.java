package ru.practicum.shareit.item.repository;

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
     * Метод добавления новой вещи в репозиторий и обновления существующей.
     *
     * @param item   объект класса {@link Item}
     * @return объект класса {@link Item} созданная вещь.
     */
    Item save(Item item);

    /**
     * Метод получения вещи по id.
     *
     * @param itemId идентификационный номер вещи.
     * @return объект класса {@link Item} запрошенный вещь.
     */
    Optional<Item> findById(Long itemId);

    /**
     * Метод удаления вещи по id.
     *
     * @param itemId идентификационный номер вещи.
     */
    void deleteById(Long itemId);

    /**
     * Метод получения списка всех вещей для указанного пользователя.
     *
     * @param userId идентификатор пользователя владельца вещей.
     * @return {@link List} объектов {@link Item} список вещей для указанного пользователя.
     */
    List<Item> findAllById(Long userId);

    /**
     * Метод поиска вещей указанному тексту.
     *
     * @param text   текс поиска.
     * @return {@link List} объектов {@link Item} список вещей удовлетворяющих параметрам поиска.
     */
    List<Item> search(String text);

    /**
     * Метод удаления всех вещей для указанного пользователя.
     *
     * @param userId идентификатор пользователя владельца вещей.
     */
    void deleteAll(Long userId);
}

