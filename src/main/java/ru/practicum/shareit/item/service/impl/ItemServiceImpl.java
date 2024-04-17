package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemServiceException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

/**
 * Реализация интерфейса {@link ItemService}
 *
 * @author Nikolay Radzivon
 * @Date 17.04.2024
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final String CHECK_USER_ERROR_MESSAGE = "Нельзя %s предмет%s для не существующего пользователя с id %d";
    private final String CHECK_ITEM_ERROR_MESSAGE = "Предмет с id %d не существует у пользователя с id %d";

    /**
     * Метод добавления нового предмета.
     *
     * @param item   объект класса {@link Item}.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @return объект класса {@link ItemDto}.
     */
    @Override
    public ItemDto addNewItem(Item item, Long userId) {
        checkUser(userId, String.format(CHECK_USER_ERROR_MESSAGE, "создать новый", "", userId));
        log.info("Создание нового предмета {} для пользователя с id {}", item, userId);
        return itemRepository.save(item, userId);
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
        checkUser(userId, String.format(CHECK_USER_ERROR_MESSAGE, "обновить", "", userId));
        log.info("Обновление предмета c id {} для пользователя с id {}, новые данные {}", itemId, userId, item);
        return itemRepository.update(item, userId, itemId);
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
