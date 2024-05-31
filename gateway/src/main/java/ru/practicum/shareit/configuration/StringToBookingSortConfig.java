package ru.practicum.shareit.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.practicum.shareit.user.converter.StringToUserSortConverter;

/**
 * @author Nikolay Radzivon
 * @Date 10.05.2024
 */
@Configuration
public class StringToBookingSortConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToUserSortConverter());
    }
}
