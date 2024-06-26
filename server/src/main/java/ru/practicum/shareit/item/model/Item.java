package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель вещь.
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
@Table(name = "items")
public class Item {
    /**
     * Уникальный идентификационный номер вещи
     */
    @Builder.Default
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;
    /**
     * Имя вещи.
     */
    @Column(name = "name", nullable = false)
    private String name;
    /**
     * Описание вещи.
     */
    @Column(name = "description", length = 512, nullable = false)
    private String description;
    /**
     * Доступность вещи для аренды.
     */
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    /**
     * Пользователь владелец вещи.
     */
    @Builder.Default
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User owner = null;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        return id != null && id.equals(((Item) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
