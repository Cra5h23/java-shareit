package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;

/**
 * Модель
 *
 * @author Nikolay Radzivon
 * @Date 19.05.2024
 */
@Getter
@Setter
@Builder(toBuilder = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "responses")
public class ItemResponse {
    /**
     * Идентификационный номер ответа.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Запрос.
     */
    @ManyToOne(targetEntity = ItemRequest.class)
    @JoinColumn(name = "request_id")
    @ToString.Exclude
    private ItemRequest request;

    /**
     * Предмет предлагаемый для запроса.
     */
    @ManyToOne(targetEntity = Item.class)
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemRequest)) return false;
        return id != null && id.equals(((ItemRequest) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
