package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Модель бронирование.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "bookings")
public class Booking {
    /**
     * Уникальный идентификационный номер бронирования.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Builder.Default
    private Long id = null;

    /**
     * Дата и время начала бронирования.
     */
    @Column(name = "start_date")
    private ZonedDateTime start;

    /**
     * Дата и время окончания бронирования.
     */
    @Column(name = "end_date")
    private ZonedDateTime end;

    /**
     * Вещь котору бронируют.
     */
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Item.class)
    @JoinColumn(name = "item_id")
    @Builder.Default
    @ToString.Exclude
    private Item item = null;

    /**
     * Пользователь, который бронирует.
     */
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "user_id")
    @Builder.Default
    @ToString.Exclude
    private User booker = null;

    /**
     * Статус бронирования.
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus status = BookingStatus.WAITING;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        return id != null && id.equals(((Booking) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
