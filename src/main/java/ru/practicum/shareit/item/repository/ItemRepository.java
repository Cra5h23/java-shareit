package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Метод поиска вещи по её названию или описанию и вывода постранично.
     *
     * @param text текс поиска.
     * @param pageable страница.
     * @return {@link Page} {@link Item}.
     */
    @Query(" select i " +
            "from Item i " +
            "where (upper(i.name) like upper(concat('%', :text, '%')) " +
            " or upper(i.description) like upper(concat('%', :text, '%'))) " +
            "and i.available = true ")
    Page<Item> searchItem(@Param("text") String text, Pageable pageable);

    void deleteAllByOwner_Id(Long ownerId);
}

