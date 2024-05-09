package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс {@link BookingRepository} репозиторий для модели {@link Booking}.
 *
 * @author Nikolay Radzivon
 * @Date 02.05.2024
 */
public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
    /**
     * Метод получения бронирования по его id и id пользователя владельца вещи.
     *
     * @param id     {@link Long} идентификационный номер вещи.
     * @param userId {@link Long} идентификационный номер пользователя владельца вещи.
     * @return {@link Optional} {@link Booking}.
     */
    @Query("select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "join fetch i.owner as o " +
            "where b.id = :booking_id and o.id = :owner_id ")
    Optional<Booking> findBookingByIdAndUserId(@Param("booking_id") Long id,
                                               @Param("owner_id") Long userId);

    /**
     * Метод получения бронирования по его id и id пользователя владельца вещи или пользователя бронирующего вещь.
     *
     * @param id     {@link Long} идентификационный номер вещи.
     * @param userId {@link Long} идентификационный номер пользователя.
     * @return {@link Optional} {@link Booking}.
     */
    @Query("select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "join fetch i.owner as o " +
            "join fetch b.booker as bo " +
            "where b.id = :booking_id and (o.id = :user_id or bo.id = :user_id) ")
    Optional<Booking> findBookingByIdAndOwnerIdOrBookerId(@Param("booking_id") Long id,
                                                          @Param("user_id") Long userId);

    /**
     * Метод получения списка бронирований по id предмета и id пользователя уже бравшего вещь в аренду.
     *
     * @param itemId   {@link Long} идентификационный номер вещи.
     * @param bookerId {@link Long} идентификационный номер пользователя.
     * @param date     {@link ZonedDateTime} дата до которой должно быть бронирование.
     * @return {@link List} {@link Booking}.
     */
    @Query("select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "join fetch b.booker as bo " +
            "where i.id = :item_id and bo.id = :booker_id and b.status != 'REJECTED' and b.start < :date ")
    List<Booking> findByItem_IdAndBooker_Id(@Param("item_id") Long itemId,
                                            @Param("booker_id") Long bookerId,
                                            @Param("date") ZonedDateTime date);

    /**
     * Метод получения списка бронирований по id вещи.
     *
     * @param itemId {@link Long} идентификационный номер вещи.
     * @return {@link List} {@link Booking}.
     */
    @Query("select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "join fetch b.booker as bo " +
            "where i.id = :item_id ")
    List<Booking> getBookings(@Param("item_id") Long itemId);

    /**
     * Метод получения списка бронирований для списка id вещей.
     *
     * @param itemId {@link List} {@link Long} список id вещей.
     * @return {@link List} {@link Booking}.
     */
    @Query("select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "join fetch b.booker as bo " +
            "where i.id in :item_id ")
    List<Booking> getBookingsByItems(@Param("item_id") List<Long> itemId);
}
