package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
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
    private final String CHECK_USER_ERROR_MESSAGE = "Нельзя %s вещ%s для не существующего пользователя с id %d";
    private final String CHECK_ITEM_ERROR_MESSAGE = "Вещь с id %d не существует%s";
    /**
     * Метод добавления новой вещи.
     *
     * @param item   объект класса {@link Item}.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @return объект класса {@link ItemResponseDto}.
     */
    @Override
    public ItemResponseDto addNewItem(ItemRequestDto item, Long userId) {
        checkUser(userId, String.format(CHECK_USER_ERROR_MESSAGE, "создать новую", "ь", userId));
        log.info("Создание новой вещи {} для пользователя с id {}", item, userId);
        return itemRepository.save(item, userId);
    }

    /**
     * Метод обновления вещи.
     *
     * @param item   объект класса {@link Item}.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @param itemId идентификационный номер вещи.
     * @return объект класса {@link ItemResponseDto}
     */
    @Override
    public ItemResponseDto updateItem(ItemRequestDto item, Long userId, Long itemId) {
        checkUser(userId, String.format(CHECK_USER_ERROR_MESSAGE, "обновить", "ь", userId));
        log.info("Обновление вещи c id {} для пользователя с id {}, новые данные {}", itemId, userId, item);
        return itemRepository.update(item, userId, itemId);
    }

    /**
     * Метод получения вещи по его id для определённого пользователя.
     *
     * @param itemId идентификационный номер вещи.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @return объект класса {@link ItemResponseDto}
     */
    @Override
    public ItemResponseDto getItemByItemId(Long itemId, Long userId) {
        log.info("Получение вещи с id {}",itemId);
        return checkItem(itemId, userId, String.format(CHECK_ITEM_ERROR_MESSAGE, itemId,""));
    }

    /**
     * Метод удаления вещи по её id для указанного пользователя.
     *
     * @param itemId идентификационный номер вещи.
     * @param userId идентификационный номер пользователя владельца вещи.
     */
    @Override
    public void deleteItemByItemId(Long itemId, Long userId) {
        checkUser(userId, String.format(CHECK_USER_ERROR_MESSAGE, "удалить", "ь c id " + itemId, userId));
        checkItem(itemId, userId, String.format(
                CHECK_ITEM_ERROR_MESSAGE, itemId, String.format(" у пользователя с id %d", userId)));
        log.info("Удаление вещи с id {} для пользователя с id {}", itemId, userId);
        itemRepository.deleteById(itemId, userId);
    }

    /**
     * Метод получения списка всех вещей пользователя.
     *
     * @param userId идентификационный номер пользователя владельца вещей.
     * @return {@link List} объектов {@link ItemResponseDto}.
     */
    @Override
    public List<ItemResponseDto> getAllItemByUser(Long userId) {
        checkUser(userId, String.format(CHECK_USER_ERROR_MESSAGE, "получить список", "ей", userId));
        log.info("Получение списка всех вещей для пользователя с id {}", userId);
        return itemRepository.findAllById(userId);
    }

    /**
     * Метод поиска вещей по тексту.
     *
     * @param text   текст по которому будет осуществлён поиск.
     * @param userId идентификационный номер пользователя у которого будет производиться поиск.
     * @return {@link List} объектов {@link ItemResponseDto}.
     */
    @Override
    public List<ItemResponseDto> searchItemByText(String text, Long userId) {
        checkUser(userId, String.format(
                CHECK_USER_ERROR_MESSAGE, "найти список", "ей с параметром поиска" + text, userId));
        log.info("Поиск вещей для пользователя с id {} с параметром поиска {}", userId, text);
        return text != null ? itemRepository.search(text, userId) : List.of();
    }

    /**
     * Метод удаления всех вещей пользователя.
     *
     * @param userId идентификационный номер пользователя.
     */
    @Override
    public void deleteAllItemByUser(Long userId) {
        checkUser(userId, String.format(CHECK_USER_ERROR_MESSAGE, "удалить все", "и", userId));
        log.info("Удаление всех вещей для пользователя с id {}", userId);
        itemRepository.deleteAll(userId);
    }

    /**
     * Метод проверки, что пользователь существует.
     * @param userId идентификатор пользователя владельца вещи.
     * @param message текст сообщения ошибки.
     */
    private void checkUser(Long userId, String message) {
        userRepository.findById(userId).orElseThrow(() -> new ItemServiceException(message));
    }

    /**
     * Метод проверки, что вещь существует.
     *
     * @param itemId  идентификатор вещи.
     * @param userId  идентификатор пользователя владельца вещи.
     * @param message текст сообщения ошибки.
     * @return
     */
    private ItemResponseDto checkItem(Long itemId, Long userId, String message) {
      return itemRepository.findById(itemId, userId).orElseThrow(() -> new ItemServiceException(message));
    }
}
