package ru.practicum.shareit.item.exception;

/**
 * Исключение для сервиса {@link ru.practicum.shareit.item.service.ItemService}
 *
 * @author Nikolay Radzivon
 * @Date 17.04.2024
 */
public class ItemServiceException extends RuntimeException {
    public ItemServiceException(String message) {
        super(message);
    }
}
