package ru.practicum.shareit.booking.service.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.UserType;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.BookingServiceException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundBookingException;
import ru.practicum.shareit.exception.NotFoundItemException;
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

/**
 * Реализация интерфейса {@link BookingService}.
 *
 * @author Nikolay Radzivon
 * @Date 02.05.2024
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    /**
     * Метод добавления нового бронирования.
     *
     * @param booking  объект класса {@link BookingRequestDto} данные для бронирования.
     * @param userId   объект класса {@link Long} идентификационный номер бронирующего пользователя.
     * @param timeZone объект класса {@link TimeZone} часовой пояс бронирующего пользователя.
     * @return объект класса {@link BookingResponseDto}.
     */
    @Override
    @Transactional
    public BookingResponseDto addNewBooking(BookingRequestDto booking, Long userId, TimeZone timeZone) {
        var user = checkUser(userId, String.format(
                "Нельзя забронировать вещь для не существующего пользователя с id %d", userId));
        var itemId = booking.getItemId();
        var item = checkItem(itemId, String.format(
                "Нельзя забронировать не существующую вещь с id %d", itemId));

        if (!item.getAvailable()) {
            throw new BookingServiceException(
                    String.format("Нельзя забронировать не доступную для бронирования вещь с id %d", itemId));
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundBookingException(String.format(
                    "Нельзя забронировать вещь у самого себя пользователь с id %d является владельцем вещи с id %d",
                    userId, itemId));
        }

        var b = BookingMapper.toBooking(booking, user, item, timeZone);
        var save = bookingRepository.save(b);
        log.info("Добавлено новое бронирование {} для пользователя с id {} на предмет с id {} ", save, userId, itemId);

        return BookingMapper.toBookingResponseDto(save, timeZone);
    }

    /**
     * Метод подтверждения или отклонения бронирования, пользователем владельцем вещи.
     *
     * @param bookingId объект класса {@link Long} идентификационный номер бронирования.
     * @param userId    объект класса {@link Long} идентификационный номер пользователя.
     * @param approved  объект класса {@link Boolean} подтверждение бронирования.
     * @param timeZone  объект класса {@link TimeZone} часовой пояс пользователя.
     * @return объект класса {@link BookingResponseDto}.
     */
    @Override
    @Transactional
    public BookingResponseDto bookingConfirmation(Long bookingId, Long userId, Boolean approved, TimeZone timeZone) {
        Optional<Booking> bookingByIdAndUserId = bookingRepository.findBookingByIdAndUserId(bookingId, userId);
        Booking booking = bookingByIdAndUserId.orElseThrow(() -> new NotFoundBookingException(
                String.format("У пользователя с id %d не существует запроса на бронирование с id %d", userId, bookingId)));
        BookingStatus status = booking.getStatus();

        if (approved && status.equals(BookingStatus.APPROVED)) {
            throw new BookingServiceException(String.format(
                    "Владелец вещи с id %d уже подтвердил бронирование с id %d", userId, bookingId));
        }

        if (!approved && status.equals(BookingStatus.REJECTED)) {
            throw new BookingServiceException(String.format(
                    "Владелец вещи с id %d уже отклонил бронирование с id %d", userId, bookingId));
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking save = bookingRepository.save(booking);

        log.info("Пользователь владелец вещи с id {} {} бронирование {}", userId, approved ? "подтвердил" : "отклонил", save);
        return BookingMapper.toBookingResponseDto(save, timeZone);
    }

    /**
     * Метод получения бронирования для указанного пользователя.
     *
     * @param bookingId объект класса {@link Long} идентификационный номер бронирования.
     * @param userId    объект класса  {@link Long} идентификационный номер пользователя.
     * @param timeZone  объект класса {@link TimeZone} часовой пояс пользователя.
     * @return объект класса {@link BookingResponseDto}.
     */
    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getBooking(Long bookingId, Long userId, TimeZone timeZone) {
        Optional<Booking> bookingByIdAndOwnerIdOrBookerId = bookingRepository.findBookingByIdAndOwnerIdOrBookerId(bookingId, userId);
        Booking booking = bookingByIdAndOwnerIdOrBookerId.orElseThrow(() -> new NotFoundBookingException(
                String.format("У пользователя с id %d не существует бронирования с id %d", userId, bookingId)));

        log.info("Получение бронирование с id {} для пользователя с id {} . {}", bookingId, userId, booking);
        return BookingMapper.toBookingResponseDto(booking, timeZone);
    }

    /**
     * Метод получения списка бронирований для бронирующего.
     *
     * @param userId   объект класса  {@link Long} идентификационный номер пользователя.
     * @param state    объект класса {@link BookingState} состояние бронирования по которому запросить список.
     * @param timeZone объект класса {@link TimeZone} часовой пояс пользователя.
     * @return {@link List} объектов класса {@link BookingResponseDto}.
     */
    @Override
    public List<BookingResponseDto> getBookingsByBooker(Long userId, BookingState state, TimeZone timeZone) {
        checkUser(userId, String.format(
                "Нельзя получить список бронирований для не существующего пользователя с id %d", userId));

        log.info("Получение списка бронирований для пользователя с id {} и сортировкой {}", userId, state);
        return getBookings(userId, state, UserType.BOOKER, timeZone);
    }

    /**
     * Метод получения списка бронирований для владельца вещей.
     *
     * @param userId   объект класса  {@link Long} идентификационный номер пользователя владельца вещей.
     * @param state    объект класса {@link BookingState} состояние бронирования по которому запросить список.
     * @param timeZone объект класса {@link TimeZone} часовой пояс пользователя.
     * @return {@link List} объектов класса {@link BookingResponseDto}.
     */
    @Override
    public List<BookingResponseDto> getBookingByOwner(Long userId, BookingState state, TimeZone timeZone) {
        checkUser(userId, String.format(
                "Нельзя получить список забронированных вещей для не существующего пользователя с id %d", userId));
        log.info("Получение списка бронирований для пользователя владельца вещей с id {} и сортировкой {}", userId, state);
        return getBookings(userId, state, UserType.OWNER, timeZone);
    }

    private List<BookingResponseDto> getBookings(Long userId, BookingState state, UserType type, TimeZone timeZone) {
        Sort.TypedSort<Booking> bookingTypedSort = Sort.sort(Booking.class);
        Sort descending = bookingTypedSort.by(Booking::getStart).descending();

        BooleanExpression query = null;

        switch (type) {
            case OWNER:
                query = QBooking.booking.item.owner.id.eq(userId);
                break;
            case BOOKER:
                query = QBooking.booking.booker.id.eq(userId);
                break;
        }

        ZonedDateTime now = ZonedDateTime.now();

        switch (state) {
            case ALL:
                break;
            case CURRENT:
                query = query.and(QBooking.booking.start.before(now).and(QBooking.booking.end.after(now)));
                break;
            case PAST:
                query = query.and(QBooking.booking.end.before(now));
                break;
            case FUTURE:
                query = query.and(QBooking.booking.start.after(now));
                break;
            case WAITING:
                query = query.and(QBooking.booking.status.eq(BookingStatus.WAITING));
                break;
            case REJECTED:
                query = query.and(QBooking.booking.status.eq(BookingStatus.REJECTED));
                break;
        }

        Iterable<Booking> all = bookingRepository.findAll(query, descending);
        return BookingMapper.toBookingResponseDtoList(all, timeZone);
    }

    private User checkUser(Long id, String message) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundUserException(message));
    }

    private Item checkItem(Long id, String message) {
        return itemRepository.findById(id).orElseThrow(() -> new NotFoundItemException(message));
    }
}
