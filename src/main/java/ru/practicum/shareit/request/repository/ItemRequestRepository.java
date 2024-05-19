package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

/**
 * @author Nikolay Radzivon
 * @Date 19.05.2024
 */
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorId(Long requestorId, Sort sort);
}
