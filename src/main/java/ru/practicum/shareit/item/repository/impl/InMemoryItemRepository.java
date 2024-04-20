package ru.practicum.shareit.item.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.exception.ItemRepositoryException;
import ru.practicum.shareit.item.mapper.ItemMapper;
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
    private final List<Item> itemList = new ArrayList<>();

    /**
     * Метод добавления новой вещи в репозиторий.
     *
     * @param item   объект класса {@link Item}
     * @param userId идентификационный номер пользователя владельца вещи.
     * @return объект класса {@link ItemResponseDto} созданная вещь.
     */
    @Override
    public ItemResponseDto save(ItemRequestDto item, Long userId) {
        Item i = ItemMapper.toItem(item, userId);

        i.setUserId(userId);
        i.setId(++generatorId);
        itemList.add(i);
        log.info("В репозиторий добавлена новая вещь {} для пользователя с id {}", i, userId);
        return ItemMapper.toItemResponseDto(i);
    }

    /**
     * Метод обновления вещи.
     *
     * @param item   объект класса {@link Item} данные для обновления.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @param itemId идентификационный номер вещи.
     * @return объект класса {@link ItemResponseDto} обновлённая вещь.
     */
    @Override
    public ItemResponseDto update(ItemRequestDto item, Long userId, Long itemId) {
        Optional<Item> first = itemList.stream()
                .filter(i -> i.getId().equals(itemId) && i.getUserId().equals(userId))
                .findFirst();
        Item i = first.orElseThrow(() -> new ItemRepositoryException(
                String.format("У пользователя с id %d нет вещи для редактирования с id %d", userId, itemId)));

        log.info("Обновлёна вещь с id {} для пользователя с id {}, старые данные {} новые данные {}", itemId, userId, i, item);

        String name = item.getName();

        if (name != null) {
            i.setName(name);
        }

        Boolean available = item.getAvailable();

        if (available != null) {
            i.setAvailable(available);
        }

        String description = item.getDescription();

        if (description != null) {
            i.setDescription(description);
        }

        return ItemMapper.toItemResponseDto(i);
    }

    /**
     * Метод получения вещи по id.
     *
     * @param itemId идентификационный номер вещи.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @return объект класса {@link ItemResponseDto} запрошенная вещь.
     */
    @Override
    public Optional<ItemResponseDto> findById(Long itemId, Long userId) {
        log.info("Запрошена вещь с id {}", itemId);
        return itemList.stream()
                .filter(item -> item.getId().equals(itemId))
                .map(ItemMapper::toItemResponseDto)
                .findFirst();
    }

    /**
     * Метод удаления вещи по id.
     *
     * @param itemId идентификационный номер вещи.
     * @param userId идентификационный номер пользователя владельца вещи.
     */
    @Override
    public void deleteById(Long itemId, Long userId) {
        itemList.stream()
                .filter(item -> item.getId().equals(itemId) && item.getUserId().equals(userId))
                .findFirst().map(itemList::remove);
        log.info("Удалёна вещь с id {} для пользователя с id {}", itemId, userId);
    }

    /**
     * Метод получения списка всех вещей для указанного пользователя.
     *
     * @param userId идентификатор пользователя владельца вещей.
     * @return {@link List} объектов {@link ItemResponseDto} список вещей для указанного пользователя.
     */
    @Override
    public List<ItemResponseDto> findAllById(Long userId) {
        log.info("Запрошен список всех вещей для пользователя с id {}", userId);
        return itemList.stream()
                .filter(item -> item.getUserId().equals(userId))
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Метод поиска вещей по указанному тексту.
     *
     * @param text   текс поиска.
     * @param userId идентификатор пользователя владельца вещей.
     * @return {@link List} объектов {@link ItemResponseDto} список вещей для указанного пользователя удовлетворяющих параметрам поиска.
     */
    @Override
    public List<ItemResponseDto> search(String text, Long userId) {
        log.info("Поиск вещей по запросу {}", text);
        if (text.isBlank()) {
            return List.of();
        }

        return itemList.stream()
                .filter(item -> (item.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                        item.getName().equals(text.toLowerCase())) && item.getAvailable().equals(true))
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Метод удаления всех вещей для указанного пользователя.
     *
     * @param userId идентификатор пользователя владельца вещей.
     */
    @Override
    public void deleteAll(Long userId) {
        itemList.stream().filter(item -> item.getUserId().equals(userId)).map(itemList::remove);
        log.info("Удаление всех вещей у пользователя с id {}", userId);
    }
}
