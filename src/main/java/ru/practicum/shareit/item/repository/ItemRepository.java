package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Интерфейс {@link ItemRepository}
 *
 * @author Nikolay Radzivon
 * @Date 17.04.2024
 */
public interface ItemRepository extends JpaRepository<Item, Long> {
    /**
     * Метод получения списка вещей для пользователя владельца вещей.
     *
     * @param userId идентификационный номер пользователя.
     * @return {@link List} {@link Item}.
     */
    @Query("select i " +
            "from Item as i " +
            "join fetch i.owner as ow " +
            "where ow.id = :user_id")
    List<Item> findItemsByUserId(@Param("user_id") Long userId);

    /**
     * Метод поиска вещи по её названию или описанию
     *
     * @param text текс поиска.
     * @return {@link List} {@link Item}.
     */
    @Query(" select i " +
            "from Item i " +
            "where (upper(i.name) like upper(concat('%', :text, '%')) " +
            " or upper(i.description) like upper(concat('%', :text, '%'))) " +
            "and i.available = true ")
    List<Item> searchItem(@Param("text") String text);
}

