package ru.practicum.shareit.item.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса {@link ItemRepository} для хранения вещей в памяти приложения.
 *
 * @author Nikolay Radzivon
 * @Date 17.04.2024
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {
    private Long generatorId = 0L;
    private final Map<Long, Item> itemMap = new HashMap<>();

    /**
     * Метод добавления новой вещи в репозиторий или обновления существующей.
     *
     * @param item объект класса {@link Item}
     * @return объект класса {@link Item} созданная вещь.
     */
    @Override
    public Item save(Item item) {
        if (item.getId() != null && itemMap.containsKey(item.getId())) {
            log.info("Обновлена вещь с id {}", item.getId());
            itemMap.put(item.getId(), item);
        } else {
            var id = ++generatorId;
            item.setId(id);
            itemMap.put(id, item);
            log.info("В репозиторий добавлена новая вещь {} для пользователя с id{}", item, item.getOwner().getId());
        }

        return item.toBuilder().build();
    }

    /**
     * Метод получения вещи по id.
     *
     * @param itemId идентификационный номер вещи.
     * @return объект класса {@link Item} запрошенная вещь.
     */
    @Override
    public Optional<Item> findById(Long itemId) {
        log.info("Запрошена вещь с id {}", itemId);

        Item i = itemMap.get(itemId);

        if (i == null) {
            return Optional.empty();
        }

        return Optional.of(i.toBuilder().build());
    }

    /**
     * Метод удаления вещи по id.
     *
     * @param itemId идентификационный номер вещи.
     */
    @Override
    public void deleteById(Long itemId) {
        itemMap.remove(itemId);
        log.info("Удалена вещь с id {}", itemId);
    }

    /**
     * Метод получения списка всех вещей для указанного пользователя.
     *
     * @param userId идентификатор пользователя владельца вещей.
     * @return {@link List} объектов {@link Item} список вещей для указанного пользователя.
     */
    @Override
    public List<Item> findAllById(Long userId) {
        log.info("Запрошен список всех вещей для пользователя с id {}", userId);
        return itemMap.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Метод поиска вещей по указанному тексту.
     *
     * @param text текс поиска.
     * @return {@link List} объектов {@link Item} список вещей удовлетворяющих параметрам поиска.
     */
    @Override
    public List<Item> search(String text) {
        var lowerCaseText = text.toLowerCase();

        log.info("Поиск вещей по запросу {}", text);
        return itemMap.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(lowerCaseText) ||
                        item.getDescription().toLowerCase().contains(lowerCaseText)) && item.getAvailable())
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Метод удаления всех вещей для указанного пользователя.
     *
     * @param userId идентификатор пользователя владельца вещей.
     */
    @Override
    public void deleteAll(Long userId) {
        itemMap.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(item -> itemMap.remove(item.getId()));

        log.info("Удаление всех вещей у пользователя с id {}", userId);
    }
}
