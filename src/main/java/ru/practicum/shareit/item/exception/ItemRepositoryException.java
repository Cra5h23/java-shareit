package ru.practicum.shareit.item.exception;

/**
 * Исключение для репозитория {@link ru.practicum.shareit.item.repository.ItemRepository}
 *
 * @author Nikolay Radzivon
 * @Date 19.04.2024
 */
public class ItemRepositoryException extends RuntimeException {
    public ItemRepositoryException(String message) {
        super(message);
    }
}
