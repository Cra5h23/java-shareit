package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
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
     * Метод добавления новой вещи в репозиторий.
     *
     * @param item   объект класса {@link Item}
     * @param userId идентификационный номер пользователя владельца вещи.
     * @return объект класса {@link ItemResponseDto} созданная вещь.
     */
    ItemResponseDto save(ItemRequestDto item, Long userId);

    /**
     * Метод обновления вещи.
     *
     * @param item   объект класса {@link Item} данные для обновления.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @param itemId идентификационный номер вещи.
     * @return объект класса {@link ItemResponseDto} обновлённый вещи.
     */
    ItemResponseDto update(ItemRequestDto item, Long userId, Long itemId);

    /**
     * Метод получения вещи по id.
     *
     * @param itemId идентификационный номер вещи.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @return объект класса {@link ItemResponseDto} запрошенный вещь.
     */
    Optional<ItemResponseDto> findById(Long itemId, Long userId);

    /**
     * Метод удаления вещи по id.
     *
     * @param itemId идентификационный номер вещи.
     * @param userId идентификационный номер пользователя владельца вещи.
     */
    void deleteById(Long itemId, Long userId);

    /**
     * Метод получения списка всех вещей для указанного пользователя.
     *
     * @param userId идентификатор пользователя владельца вещей.
     * @return {@link List} объектов {@link ItemResponseDto} список вещей для указанного пользователя.
     */
    List<ItemResponseDto> findAllById(Long userId);

    /**
     * Метод поиска вещей для указанного пользователя по указанному тексту.
     *
     * @param text   текс поиска.
     * @param userId идентификатор пользователя владельца вещей.
     * @return {@link List} объектов {@link ItemResponseDto} список вещей удовлетворяющих параметрам поиска.
     */
    List<ItemResponseDto> search(String text, Long userId);

    /**
     * Метод удаления всех вещей для указанного пользователя.
     *
     * @param userId идентификатор пользователя владельца вещей.
     */
    void deleteAll(Long userId);
}

