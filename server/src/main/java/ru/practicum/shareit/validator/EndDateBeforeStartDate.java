package ru.practicum.shareit.validator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация проверки для класса {@link ru.practicum.shareit.booking.dto.BookingRequestDto}, что дата окончания бронирования не должна быть раньше начала бронирования.
 *
 * @author Nikolay Radzivon
 * @Date 03.05.2024
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EndDateBeforeStartDateValidator.class)
public @interface EndDateBeforeStartDate {
    String message() default "Дата окончания бронирования не должна быть раньше даты начала бронирования";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
} //todo вся папка в модуль gateway
