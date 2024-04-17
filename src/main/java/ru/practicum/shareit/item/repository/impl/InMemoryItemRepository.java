package ru.practicum.shareit.item.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.*;

/**
 * Реализация интерфейса {@link ItemRepository} для хранения предметов в памяти приложения.
 *
 * @author Nikolay Radzivon
 * @Date 17.04.2024
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, List<Item>> itemMap = new HashMap<>();
    private Long generatorId = 0L;
    private final ItemMapper itemMapper;

    /**
     * Метод добавления нового предмета в репозиторий.
     *
     * @param item   объект класса {@link Item}
     * @param userId идентификационный номер пользователя владельца предмета.
     * @return объект класса {@link ItemDto} созданный предмет.
     */
    @Override
    public ItemDto save(Item item, Long userId) {
        List<Item> items = itemMap.getOrDefault(userId, new ArrayList<>());
        item.setId(++generatorId);
        if (!items.contains(item)) {
            items.add(item);
        }
        itemMap.put(userId, items);
        log.info("В репозиторий для пользователя {} добавлен новый предмет {} и присвоен id {}", userId, item, item.getId());
        return itemMapper.toItemDto(item);
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
