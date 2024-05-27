package ru.practicum.shareit.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Nikolay Radzivon
 * @Date 27.05.2024
 */
class EndDateBeforeStartDateValidatorTest {
    EndDateBeforeStartDateValidator endDateBeforeStartDateValidator;
    ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    void setUp() {
        endDateBeforeStartDateValidator = new EndDateBeforeStartDateValidator();
        constraintValidatorContext = Mockito.mock(ConstraintValidatorContext.class);
    }

    @Test
    void isValidTestFalse() {
        var now = LocalDateTime.now();
        var bookingRequestDto = BookingRequestDto.builder()
                .start(now.plusDays(1))
                .end(now.minusDays(1))
                .itemId(1L).build();
        boolean valid = endDateBeforeStartDateValidator.isValid(bookingRequestDto, constraintValidatorContext);

        assertFalse(valid);
    }

    @Test
    void isValidTestStartDateNull() {
        var now = LocalDateTime.now();
        var bookingRequestDto = BookingRequestDto.builder()
                .start(null)
                .end(now.minusDays(1))
                .itemId(1L).build();
        boolean valid = endDateBeforeStartDateValidator.isValid(bookingRequestDto, constraintValidatorContext);

        assertTrue(valid);
    }

    @Test
    void isValidTestEndDateNull() {
        var now = LocalDateTime.now();
        var bookingRequestDto = BookingRequestDto.builder()
                .start(now)
                .end(null)
                .itemId(1L).build();
        boolean valid = endDateBeforeStartDateValidator.isValid(bookingRequestDto, constraintValidatorContext);

        assertTrue(valid);
    }

    @Test
    void isValidTestEndDataAfterStartDate() {
        var now = LocalDateTime.now();
        var bookingRequestDto = BookingRequestDto.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .itemId(1L).build();
        boolean valid = endDateBeforeStartDateValidator.isValid(bookingRequestDto, constraintValidatorContext);

        assertTrue(valid);
    }
}