package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemResponse;

/**
 * @author Nikolay Radzivon
 * @Date 19.05.2024
 */
public interface ItemResponseRepository extends JpaRepository<ItemResponse, Long> {
}
