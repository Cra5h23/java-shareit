package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Модель комментарий {@link Comment}
 *
 * @author Nikolay Radzivon
 * @Date 07.05.2024
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "comments")
public class Comment {
    /**
     * Идентификационный номер комментария.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Builder.Default
    private Long id = null;

    /**
     * Текст комментария.
     */
    @Column(name = "text")
    private String text;

    /**
     * Предмет которому оставлен комментарий.
     */
    @ManyToOne(targetEntity = Item.class)
    @JoinColumn(name = "item_id")
    @Builder.Default
    @ToString.Exclude
    private Item item = null;

    /**
     * Автор комментария.
     */
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "user_id")
    @Builder.Default
    @ToString.Exclude
    private User author = null;

    /**
     * Дата и время создания комментария.
     */
    @Column(name = "created")
    private ZonedDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;
        return id != null && id.equals(((Comment) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
