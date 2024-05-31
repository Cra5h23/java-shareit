package ru.practicum.shareit.validator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация класса {@link ru.practicum.shareit.booking.dto.BookingRequestDto} для проверки, что дата старта бронирования не равна дате окончания бронирования.
 *
 * @author Nikolay Radzivon
 * @Date 03.05.2024
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartDateEqualsEndDateValidator.class)
public @interface StartDateEqualsEndDate {
    String message() default "Дата старта бронирования не должна совпадать с датой окончания бронирования";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
