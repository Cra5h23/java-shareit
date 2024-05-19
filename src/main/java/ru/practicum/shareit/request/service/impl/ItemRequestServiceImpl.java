package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.UserChecker;
import ru.practicum.shareit.exception.NotFoundItemRequestException;
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
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserChecker userChecker;
    private final Sort.TypedSort<ItemRequest> typedSort = Sort.sort(ItemRequest.class);

    /**
     * Метод создания нового запроса вещи.
     *
     * @param request  {@link ItemRequestDtoRequest} описание вещи которая нужна.
     * @param userId   {@link Long} идентификационный номер пользователя создающего запрос.
     * @param timeZone {@link TimeZone} часовой пояс пользователя создающего запрос.
     * @return {@link ItemRequestDtoResponse} данные запроса.
     */
    @Override
    @Transactional
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
    @Transactional(readOnly = true)
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

    /**
     * Метод получения всех запросов.
     *
     * @param userId идентификационный номер пользователя.
     * @param from   индекс первого элемента начиная с 0.
     * @param size   количество элементов для отображения.
     * @return постраничный список запросов.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoResponse> getAllRequests(Long userId, Integer from, Integer size) {
        log.info("Запрошен список всех запросов с параметрами from={}, size={}", from, size);
        if (from == null || size == null) {
            return List.of();
        }

        userChecker.checkUser(userId, String.format(
                "Нельзя запросить список всех запросов от не существующего пользователя с id %d", userId));

        var sort = typedSort.by(ItemRequest::getCreated).descending();
        var pageable = PageRequest.of(from, size, sort);
        var requests = itemRequestRepository.findAllByRequestorIdNot(userId, pageable);

        return requests.map(ItemRequestMapper::toItemRequestDtoResponse).stream().collect(Collectors.toList());
    }

    /**
     * Метод получения запроса по его идентификационному номеру.
     *
     * @param requestId {@link Long} идентификационный номер запроса.
     * @param userId    {@link Long} идентификационный номер пользователя.
     * @return {@link ItemRequestDtoResponse}
     */
    @Override
    @Transactional(readOnly = true)
    public ItemRequestDtoResponse getRequestById(Long requestId, Long userId) {
        userChecker.checkUser(userId, String.format(
                "Нельзя получить запрос не существующим пользователем с id %d", userId));

        var byId = itemRequestRepository.findById(requestId);
        var itemRequest = byId.orElseThrow(()-> new NotFoundItemRequestException(
                String.format("Запрос с id %d не существует", requestId)));
        log.info("Запрошен запрос с id {} от пользователя с id {}", requestId, userId);

        return ItemRequestMapper.toItemRequestDtoResponse(itemRequest);
    }
}
