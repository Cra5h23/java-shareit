package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

/**
 * Интерфейс {@link UserRepository}.
 *
 * @author Nikolay Radzivon.
 */
public interface UserRepository extends JpaRepository<User, Long> {
}