package ru.practicum.shareit.exception;

/**
 * @author Nikolay Radzivon
 * @Date 03.05.2024
 */
public class NotFoundItemException extends RuntimeException{
    public NotFoundItemException(String message) {
        super(message);
    }
}
