package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * @author Nikolay Radzivon
 * @Date 19.05.2024
 */
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
}
