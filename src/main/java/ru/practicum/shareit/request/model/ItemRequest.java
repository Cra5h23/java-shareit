package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Модель запроса на вещь.
 *
 * @author Nikolay Radzivon
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "requests")
public class ItemRequest {
    /**
     * Идентификационный номер запроса.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Описание вещи которую хотят забронировать.
     */
    @Column(name = "description", nullable = false, length = 512)
    private String description;

    /**
     * Пользователь создатель запроса.
     */
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;

    /**
     * Дата и время создания запроса.
     */
    @Column(name = "created_data", nullable = false)
    private ZonedDateTime created;

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
