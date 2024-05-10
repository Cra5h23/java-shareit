package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

/**
 * Интерфейс {@link CommentRepository}
 *
 * @author Nikolay Radzivon
 * @Date 07.05.2024
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * Метод получения списка комментариев по id вещи.
     *
     * @param itemId идентификационный номер вещи.
     * @return {@link List} {@link Comment}
     */
    List<Comment> findByItem_id(Long itemId);
}
