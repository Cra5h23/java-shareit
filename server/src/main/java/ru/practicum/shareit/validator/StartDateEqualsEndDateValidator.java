package ru.practicum.shareit.validator;

import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Валидатор для аннотации {@link StartDateEqualsEndDate}
 *
 * @author Nikolay Radzivon
 * @Date 03.05.2024
 */
public class StartDateEqualsEndDateValidator implements ConstraintValidator<StartDateEqualsEndDate, BookingRequestDto> {
    @Override
    public boolean isValid(BookingRequestDto bookingRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        if (bookingRequestDto.getEnd() == null || bookingRequestDto.getStart() == null) return true;

        return !bookingRequestDto.getStart().equals(bookingRequestDto.getEnd());
    }
}
