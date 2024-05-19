package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoCreated;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.TimeZone;

/**
 * Интерфейс {@link ItemRequestService} для логики работы с запросами вещей.
 *
 * @author Nikolay Radzivon
 * @Date 18.05.2024
 */
public interface ItemRequestService {
    /**
     * Метод создания нового запроса вещи.
     *
     * @param request  {@link ItemRequestDtoRequest} описание вещи которая нужна.
     * @param userId   {@link Long} идентификационный номер пользователя создающего запрос.
     * @param timeZone {@link TimeZone} часовой пояс пользователя создающего запрос.
     * @return {@link ItemRequestDtoCreated} данные запроса.
     */
    ItemRequestDtoCreated addNewRequest(ItemRequestDtoRequest request, Long userId, TimeZone timeZone);
}
