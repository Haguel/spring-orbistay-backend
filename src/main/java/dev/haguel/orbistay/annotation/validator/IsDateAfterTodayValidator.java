package dev.haguel.orbistay.annotation.validator;

import dev.haguel.orbistay.annotation.IsDateAfterToday;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidator;

import java.time.LocalDate;

public class IsDateAfterTodayValidator implements ConstraintValidator<IsDateAfterToday, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalDate today = LocalDate.now();
        LocalDate date = LocalDate.parse(value);
        return date.isAfter(today);
    }
}