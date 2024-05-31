package ru.practicum.shareit.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.practicum.shareit.booking.converter.StringToBookingStateConverter;

/**
 * Класс конфигурация для {@link StringToBookingStateConverter}
 *
 * @author Nikolay Radzivon
 * @Date 05.05.2024
 */
@Configuration
public class StringToBookingStateConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToBookingStateConverter());
    }
}
