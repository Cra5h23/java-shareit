package ru.practicum.shareit.checker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

/**
 * @author Nikolay Radzivon
 * @Date 23.05.2024
 */
@Component
@RequiredArgsConstructor
public class ItemChecker {
    private final ItemRepository itemRepository;

    public Item checkItem(Long itemId, String message) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundItemException(message));
    }
}
