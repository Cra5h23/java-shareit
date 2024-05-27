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
class StartDateEqualsEndDateValidatorTest {
    StartDateEqualsEndDateValidator startDateEqualsEndDateValidator;
    ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    void setUp() {
        startDateEqualsEndDateValidator = new StartDateEqualsEndDateValidator();
        constraintValidatorContext = Mockito.mock(ConstraintValidatorContext.class);
    }

    @Test
    void isValidTestFalse() {
        var now = LocalDateTime.now();
        var bookingRequestDto = BookingRequestDto.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(1))
                .itemId(1L).build();
        boolean valid = startDateEqualsEndDateValidator.isValid(bookingRequestDto, constraintValidatorContext);

        assertFalse(valid);
    }

    @Test
    void isValidTestStartDateNull() {
        var now = LocalDateTime.now();
        var bookingRequestDto = BookingRequestDto.builder()
                .start(null)
                .end(now.minusDays(1))
                .itemId(1L).build();
        boolean valid = startDateEqualsEndDateValidator.isValid(bookingRequestDto, constraintValidatorContext);

        assertTrue(valid);
    }

    @Test
    void isValidTestEndDateNull() {
        var now = LocalDateTime.now();
        var bookingRequestDto = BookingRequestDto.builder()
                .start(now)
                .end(null)
                .itemId(1L).build();
        boolean valid = startDateEqualsEndDateValidator.isValid(bookingRequestDto, constraintValidatorContext);

        assertTrue(valid);
    }
}