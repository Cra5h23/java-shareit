package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.exception.ItemServiceException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final String checkUserErrorMessage = "Нельзя %s вещ%s для не существующего пользователя с id %d";

    /**
     * Метод добавления новой вещи.
     *
     * @param item   объект класса {@link ItemRequestDto}.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @return объект класса {@link ItemResponseDto}.
     */
    @Override
    public ItemResponseDto addNewItem(ItemRequestDto item, Long userId) {
        User user = checkUser(userId, String.format(checkUserErrorMessage, "создать новую", "ь", userId));
        Item i = ItemMapper.toItem(item, user);
        Item save = itemRepository.save(i);

        log.info("Создана новая вещь {} для пользователя с id {} и присвоен id {}", item, userId, save.getId());
        return ItemMapper.toItemResponseDto(i);
    }

    /**
     * Метод обновления вещи.
     *
     * @param item   объект класса {@link ItemRequestDto}.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @param itemId идентификационный номер вещи.
     * @return объект класса {@link ItemResponseDto}
     */
    @Override
    public ItemResponseDto updateItem(ItemRequestDto item, Long userId, Long itemId) {
        checkUser(userId, String.format(checkUserErrorMessage, "обновить", "ь", userId));

        Item i = checkItem(itemId, userId);
        String name = item.getName();

        log.info("Обновление вещи c id {} для пользователя с id {}, старые данные новые {} данные {}", itemId, userId, i, item);
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

        Item save = itemRepository.save(i);

        return ItemMapper.toItemResponseDto(save);
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
        log.info("Получение вещи с id {}", itemId);
        Item i = checkItem(itemId, null);
        return ItemMapper.toItemResponseDto(i);
    }

    /**
     * Метод удаления вещи по её id для указанного пользователя.
     *
     * @param itemId идентификационный номер вещи.
     * @param userId идентификационный номер пользователя владельца вещи.
     */
    @Override
    public void deleteItemByItemId(Long itemId, Long userId) {
        checkUser(userId, String.format(checkUserErrorMessage, "удалить", "ь c id " + itemId, userId));
        checkItem(itemId, userId);
        log.info("Удаление вещи с id {} для пользователя с id {}", itemId, userId);
        itemRepository.deleteById(itemId);
    }

    /**
     * Метод получения списка всех вещей пользователя.
     *
     * @param userId идентификационный номер пользователя владельца вещей.
     * @return {@link List} объектов {@link ItemResponseDto}.
     */
    @Override
    public List<ItemResponseDto> getAllItemByUser(Long userId) {
        checkUser(userId, String.format(checkUserErrorMessage, "получить список", "ей", userId));
        log.info("Получение списка всех вещей для пользователя с id {}", userId);
        List<Item> allById = itemRepository.findAllById(userId);
        return allById.stream().map(ItemMapper::toItemResponseDto).collect(Collectors.toList());
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
        if (text.isBlank()) {
            return List.of();
        }

        checkUser(userId, String.format(
                checkUserErrorMessage, "найти список", "ей с параметром поиска" + text, userId));
        log.info("Поиск вещей для пользователя с id {} с параметром поиска {}", userId, text);

        List<Item> search = itemRepository.search(text);

        return search.stream().map(ItemMapper::toItemResponseDto).collect(Collectors.toList());
    }

    /**
     * Метод удаления всех вещей пользователя.
     *
     * @param userId идентификационный номер пользователя.
     */
    @Override
    public void deleteAllItemByUser(Long userId) {
        checkUser(userId, String.format(checkUserErrorMessage, "удалить все", "и", userId));
        log.info("Удаление всех вещей для пользователя с id {}", userId);
        itemRepository.deleteAll(userId);
    }

    /**
     * Метод проверки, что пользователь существует.
     *
     * @param userId  идентификатор пользователя владельца вещи.
     * @param message текст сообщения ошибки.
     */
    private User checkUser(Long userId, String message) {
        return userRepository.findById(userId).orElseThrow(() -> new ItemServiceException(message));
    }

    /**
     * Метод проверки, что вещь существует.
     *
     * @param itemId идентификатор вещи.
     * @param userId идентификатор пользователя владельца вещи.
     * @return объект класса {@link Item}
     */
    private Item checkItem(Long itemId, Long userId) {
        var s = "Вещь с id %d не существует у пользователя с id %d";
        Optional<Item> byId = itemRepository.findById(itemId);

        if (userId != null) {
            if (byId.isEmpty()) {
                throw new ItemServiceException(String.format(s, itemId, userId));
            }
            if (byId.get().getOwner().getId().equals(userId)) {
                return byId.get();
            }
            throw new ItemServiceException(String.format(s, itemId, userId));
        } else {
            if (byId.isEmpty()) {
                throw new ItemServiceException(String.format(s.substring(0, 26), itemId));
            }
            return byId.get();
        }
    }
}
