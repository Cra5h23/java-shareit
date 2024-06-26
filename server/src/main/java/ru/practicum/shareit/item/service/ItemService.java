package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.TimeZone;

/**
 * Интерфейс {@link ItemService}
 *
 * @author Nikolay Radzivon
 * @Date 17.04.2024
 */
public interface ItemService {
    /**
     * Метод добавления новой вещи.
     *
     * @param item   объект класса {@link Item}.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @return объект класса {@link ItemDtoResponse}.
     */
    ItemDtoResponse addNewItem(ItemDtoRequest item, Long userId);

    /**
     * Метод обновления вещи.
     *
     * @param item   объект класса {@link Item}.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @param itemId идентификационный номер вещи.
     * @return объект класса {@link ItemDtoResponse}
     */
    ItemDtoResponse updateItem(ItemDtoRequest item, Long userId, Long itemId);

    /**
     * Метод получения вещи по его id для определённого пользователя.
     *
     * @param itemId идентификационный номер вещи.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @return объект класса {@link ItemDtoResponse}
     */
    OwnerItemResponseDto getItemByItemId(Long itemId, Long userId);

    /**
     * Метод удаления вещи по его id для указанного пользователя.
     *
     * @param itemId идентификационный номер вещи.
     * @param userId идентификационный номер пользователя владельца вещи.
     */
    void deleteItemByItemId(Long itemId, Long userId);

    /**
     * Метод получения списка всех вещей пользователя.
     *
     * @param userId идентификационный номер пользователя владельца вещей.
     * @return {@link List} объектов {@link ItemDtoResponse}.
     */
    List<OwnerItemResponseDto> getAllItemByUser(Long userId, Integer from, Integer size);

    /**
     * Метод поиска вещей по тексту.
     *
     * @param params параметры запроса.
     * @return {@link List} объектов {@link ItemDtoResponse}.
     */
    List<ItemDtoResponse> searchItemByText(ItemSearchParams params);

    /**
     * Метод удаления всех вещей пользователя.
     *
     * @param userId идентификационный номер пользователя.
     */
    void deleteAllItemByUser(Long userId);

    /**
     * Метод добавления комментария вещи.
     *
     * @param itemId   идентификационный номер вещи.
     * @param userId   идентификационный номер пользователя.
     * @param timeZone часовой пояс пользователя.
     * @param text     текст комментария.
     * @return {@link CommentResponseDto}
     */
    CommentResponseDto addComment(Long itemId, Long userId, TimeZone timeZone, CommentRequestDto text);

    CommentResponseDto updateComment(CommentRequestDto comment, Long userId, Long commentId);

    void deleteComment(Long commentId, Long userId);
}
