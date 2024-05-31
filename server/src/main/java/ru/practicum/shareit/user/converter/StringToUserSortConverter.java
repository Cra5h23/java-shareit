package ru.practicum.shareit.user.converter;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.user.exeption.UserSortException;
import ru.practicum.shareit.user.model.UserSort;

/**
 * @author Nikolay Radzivon
 * @Date 10.05.2024
 */
public class StringToUserSortConverter implements Converter<String, UserSort> {
    @Override
    public UserSort convert(String source) {
        try {
            return UserSort.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            String ex = message.substring(message.lastIndexOf(".") + 1);
            throw new UserSortException(ex);
        }
    }
}
