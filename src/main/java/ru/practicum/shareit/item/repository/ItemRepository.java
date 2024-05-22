package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Интерфейс {@link ItemRepository}
 *
 * @author Nikolay Radzivon
 * @Date 17.04.2024
 */
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {
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

    void deleteAllByOwner_Id(Long ownerId);
}

