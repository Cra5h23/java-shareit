package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;

/**
 * Модель пользователя.
 *
 * @author Nikolay Radzivon
 */
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    /**
     * Идентификационный номер пользователя.
     */
    @Builder.Default
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;
    /**
     * Имя пользователя.
     */
    @Column(name = "name")
    private String name;
    /**
     * Электронная почта пользователя.
     */
    @Column(name = "email", unique = true, length = 512)
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return id != null && id.equals(((User) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
