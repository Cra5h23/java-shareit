package ru.practicum.shareit.item.exception;

/**
 * @author Nikolay Radzivon
 * @Date 10.05.2024
 */
public class NotFoundCommentException extends RuntimeException {
    public NotFoundCommentException(String message) {
        super(message);
    }
}
