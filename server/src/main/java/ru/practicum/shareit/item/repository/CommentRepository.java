package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

/**
 * Интерфейс {@link CommentRepository}
 *
 * @author Nikolay Radzivon
 * @Date 07.05.2024
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
