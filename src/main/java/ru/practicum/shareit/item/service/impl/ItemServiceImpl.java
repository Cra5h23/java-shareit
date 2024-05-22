package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundBookingException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exception.ItemServiceException;
import ru.practicum.shareit.item.exception.NotFoundCommentException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemSearchParams;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.mapper.ItemResponseMapper;
import ru.practicum.shareit.request.repository.ItemResponseRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
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
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final String checkUserErrorMessage = "Нельзя %s вещ%s для не существующего пользователя с id %d";
    private final ItemResponseRepository itemResponseRepository;
    private final Sort.TypedSort<Item> typedSort = Sort.sort(Item.class);

    /**
     * Метод добавления новой вещи.
     *
     * @param item   объект класса {@link ItemDtoRequest}.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @return объект класса {@link ItemDtoResponse}.
     */
    @Override
    @Transactional
    public ItemDtoResponse addNewItem(ItemDtoRequest item, Long userId) {
        var user = checkUser(userId, String.format(checkUserErrorMessage, "создать новую", "ь", userId));
        var i = ItemMapper.toItem(item, user);
        var save = itemRepository.save(i);
        var requestId = item.getRequestId();

        if (requestId != null) {
            var itemResponse = ItemResponseMapper.toItemResponse(i, item.getRequestId());
            var response = itemResponseRepository.save(itemResponse);

            log.info("Создана новая вещь {} для пользователя с id {} и присвоен id {}, для запроса с id {} и ответу присвоен id {}",
                    item, userId, save.getId(), requestId, response.getId());
            return ItemMapper.toItemResponseDto(i, requestId);
        }

        log.info("Создана новая вещь {} для пользователя с id {} и присвоен id {}", item, userId, save.getId());
        return ItemMapper.toItemResponseDto(i);
    }

    /**
     * Метод обновления вещи.
     *
     * @param item   объект класса {@link ItemDtoRequest}.
     * @param userId идентификационный номер пользователя владельца вещи.
     * @param itemId идентификационный номер вещи.
     * @return объект класса {@link ItemDtoResponse}
     */
    @Override
    @Transactional
    public ItemDtoResponse updateItem(ItemDtoRequest item, Long userId, Long itemId) {
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
     * @return объект класса {@link ItemDtoResponse}
     */
    @Override
    @Transactional(readOnly = true)
    public OwnerItemResponseDto getItemByItemId(Long itemId, Long userId) {
        log.info("Получение вещи с id {}", itemId);
        Item i = checkItem(itemId, null);

        Long id = i.getOwner().getId();

        BookingShort lastBooking = null;
        BookingShort nextBooking = null;

        if (id.equals(userId)) {
            List<Booking> bookings = bookingRepository.getBookings(itemId);
            ZonedDateTime now = ZonedDateTime.now();

            Optional<Booking> last = bookings.stream()
                    .sorted(Comparator.comparing(Booking::getEnd))
                    .filter(b -> b.getEnd().isBefore(now) ||
                            (b.getStart().isBefore(now) && b.getEnd().isAfter(now)))
                    .max(Comparator.comparing(Booking::getId));
            lastBooking = last.map(BookingMapper::toBookingShort).orElse(null);

            if (lastBooking != null) {
                Optional<Booking> next = bookings.stream()
                        .sorted(Comparator.comparing(Booking::getStart))
                        .filter(b -> b.getStart().isAfter(now))
                        .max(Comparator.comparing(Booking::getId));

                log.info("next {}", next);
                nextBooking = next
                        .map(BookingMapper::toBookingShort)
                        .orElse(null);
            }
        }

        return ItemMapper.toOwnerItemResponseDto(i, lastBooking, nextBooking);
    }

    /**
     * Метод удаления вещи по её id для указанного пользователя.
     *
     * @param itemId идентификационный номер вещи.
     * @param userId идентификационный номер пользователя владельца вещи.
     */
    @Override
    @Transactional
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
     * @return {@link List} объектов {@link ItemDtoResponse}.
     */
    @Override
    @Transactional(readOnly = true)
    public List<OwnerItemResponseDto> getAllItemByUser(Long userId, Integer from, Integer size) {
        checkUser(userId, String.format(checkUserErrorMessage, "получить список", "ей", userId));
        log.info("Получение списка всех вещей для пользователя с id {} с параметрами from={}, size={}", userId, from, size);

        if (from == null || size == null) {
            return List.of();
        }
        var sort = typedSort.by(Item::getId);
        var pageable = PageRequest.of(from, size, sort);

        Page<Item> allByUserId = itemRepository.findAllByOwnerId(userId, pageable);

        List<Long> collect = allByUserId.stream().map(Item::getId).collect(Collectors.toList());

        List<Booking> bookingsByItems = bookingRepository.getBookingsByItems(collect);
        ZonedDateTime now = ZonedDateTime.now();
        List<OwnerItemResponseDto> dtoList = new ArrayList<>();

        for (Item item : allByUserId) {
            BookingShort lastBooking = null;
            BookingShort nextBooking = null;

            Optional<Booking> last = bookingsByItems.stream()
                    .filter(b -> b.getItem().getId().equals(item.getId()))
                    .sorted(Comparator.comparing(Booking::getEnd))
                    .filter(b -> b.getEnd().isBefore(now) ||
                            (b.getStart().isBefore(now) && b.getStart().isAfter(now)))
                    .max(Comparator.comparing(Booking::getId));
            lastBooking = last.map(BookingMapper::toBookingShort).orElse(null);

            if (lastBooking != null) {
                Optional<Booking> next = bookingsByItems.stream()
                        .filter(b -> b.getItem().getId().equals(item.getId()))
                        .sorted(Comparator.comparing(Booking::getStart))
                        .filter(b -> b.getStart().isAfter(now))
                        .max(Comparator.comparing(Booking::getId));
                nextBooking = next
                        .map(BookingMapper::toBookingShort)
                        .orElse(null);
            }

            OwnerItemResponseDto dto = ItemMapper.toOwnerItemResponseDto(item, lastBooking, nextBooking);
            dtoList.add(dto);
        }

        return dtoList;
    }

    /**
     * Метод поиска вещей по тексту.
     *
     * @param params {@link ItemSearchParams} параметры поиска включают в себя (userId - идентификатор пользователя, text - текст поиска, from - индекс первого элемента, size - количество элементов для отображения).
     * @return {@link List} объектов {@link ItemDtoResponse}.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoResponse> searchItemByText(@Valid ItemSearchParams params) {
        var text = params.getText();

        if (text.isBlank()) {
            return List.of();
        }

        var userId = params.getUserId();
        checkUser(params.getUserId(), String.format(
                checkUserErrorMessage, "найти список", "ей с параметром поиска" + text, userId));

        var from = params.getFrom();
        var size = params.getSize();

        log.info("Поиск вещей для пользователя с id {} с параметрами поиска text={}, from={}, size={}", userId, text, from, size);

        var pageable = PageRequest.of(from, size);

        Page<Item> items = itemRepository.searchItem(text, pageable);

        return items.stream().map(ItemMapper::toItemResponseDto).collect(Collectors.toList());
    }

    /**
     * Метод удаления всех вещей пользователя.
     *
     * @param userId идентификационный номер пользователя.
     */
    @Override
    @Transactional
    public void deleteAllItemByUser(Long userId) {
        checkUser(userId, String.format(checkUserErrorMessage, "удалить все", "и", userId));
        log.info("Удаление всех вещей для пользователя с id {}", userId);
        itemRepository.deleteAllByOwner_Id(userId);
    }

    /**
     * Метод добавления комментария вещи.
     *
     * @param itemId   идентификационный номер вещи.
     * @param userId   идентификационный номер пользователя.
     * @param timeZone часовой пояс пользователя.
     * @param text     текст комментария.
     * @return {@link CommentResponseDto}
     */
    @Override
    @Transactional
    public CommentResponseDto addComment(Long itemId, Long userId, TimeZone timeZone, CommentRequestDto text) {
        Item item = checkItem(itemId, null);
        User user = checkUser(userId, String.format(
                "Нельзя оставить комментарий от не существующего пользователя с id %d", userId));
        ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(timeZone.toZoneId());
        List<Booking> byBookerId = bookingRepository.findByItem_IdAndBooker_Id(itemId, userId, zonedDateTime);

        if (byBookerId.isEmpty()) {
            throw new NotFoundBookingException(String.format(
                    "Нельзя оставить комментарий. Пользователь с id %d ещё не бронировал вещь с id %d", userId, itemId));
        }

        Comment comment = CommentMapper.toComment(text, item, user, timeZone);

        Comment save = commentRepository.save(comment);
        log.info("Добавлен новый комментарий для предмета с id {} от пользователя с id {} ,{}", itemId, userId, save);
        return CommentMapper.toCommentResponseDto(save);
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(CommentRequestDto comment, Long userId, Long commentId) {
        log.info("Обновление комментария с id {} для пользователя с id {}", commentId, userId);
        checkUser(userId, String.format("Нельзя обновить комментарий для не существующего пользователя с id %d", userId));
        Optional<Comment> commentOptional = commentRepository.findById(commentId);

        Comment c = commentOptional.orElseThrow(
                () -> new NotFoundCommentException(String.format("Нет комментария с id %d", commentId)));

        if (!c.getAuthor().getId().equals(userId)) {
            throw new NotFoundCommentException(String.format(
                    "У пользователя с id %d нет комментария с id %d", userId, commentId));
        }
        log.info("Новые данные {} , старые данные {}", comment, c);
        c.setText(comment.getText());
        Comment save = commentRepository.save(c);

        return CommentMapper.toCommentResponseDto(save);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        checkUser(userId, String.format(
                "Нельзя удалить коментарий для не существующего пользователя с id %d", userId));
        Optional<Comment> byId = commentRepository.findById(commentId);
        Comment comment = byId.orElseThrow(() -> new NotFoundCommentException(
                String.format("Нельзя удалить не существующий комментарий с id %d", commentId)));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new NotFoundCommentException(String.format(
                    "У пользователя с id %d нет комментария с id %d", userId, commentId));
        }
        log.info("Удалён комментарий с id {} для пользователя с id {}", commentId, userId);
        commentRepository.deleteById(commentId);
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
