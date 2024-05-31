package ru.practicum.shareit.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * @author Nikolay Radzivon
 * @Date 21.04.2024
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Java-Shareit Api",
                description = "Api для приложения Java-Shareit",
                version = "1.0.0",
                contact = @Contact(
                        name = "Nikolay Radzivon"
                )
        )
)
public class OpenApiConfig {
}
