package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.UserChecker;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreated;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * @author Nikolay Radzivon
 * @Date 19.05.2024
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserChecker userChecker;

    /**
     * Метод создания нового запроса вещи.
     *
     * @param request  {@link ItemRequestDtoRequest} описание вещи которая нужна.
     * @param userId   {@link Long} идентификационный номер пользователя создающего запрос.
     * @param timeZone {@link TimeZone} часовой пояс пользователя создающего запрос.
     * @return {@link ItemRequestDtoResponse} данные запроса.
     */
    @Override
    public ItemRequestDtoCreated addNewRequest(ItemRequestDtoRequest request, Long userId, TimeZone timeZone) {
        log.info("Пользователь с id {} создаёт запрос {}", userId, request);

        var user = userChecker.checkUser(userId, String.format(
                "Нельзя создать новый запрос для не существующего пользователя с id %d.", userId));
        var itemRequest = ItemRequestMapper.toItemRequest(request, user, timeZone);
        var save = itemRequestRepository.save(itemRequest);
        var itemRequestDtoCreated = ItemRequestMapper.toItemRequestDtoCreated(save);

        log.info("Создан запрос {}", itemRequestDtoCreated);
        return itemRequestDtoCreated;
    }

    /**
     * Метод получения всех запросов для указанного пользователя.
     *
     * @param userId {@link Long} идентификационный номер пользователя.
     * @return список запросов.
     */
    @Override
    public List<ItemRequestDtoResponse> getUserRequests(Long userId) {
        userChecker.checkUser(userId, String.format(
                "Нельзя запросить список запросов для не существующего пользователя с id %d.", userId));

        var typedSort = Sort.sort(ItemRequest.class);
        var sort = typedSort.by(ItemRequest::getCreated).descending();
        var requests = itemRequestRepository.findAllByRequestorId(userId, sort);
        var requestsDto = requests.stream()
                .map(ItemRequestMapper::toItemRequestDtoResponse)
                .collect(Collectors.toList());

        log.info("Запрошен список всех запросов для пользователя с id {}", userId);
        return requestsDto;
    }
}
