package dev.haguel.orbistay.annotation.validator;

import dev.haguel.orbistay.annotation.ValidDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateValidator implements ConstraintValidator<ValidDate, String> {

    private static final String DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";

    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }
        return date.matches(DATE_PATTERN);
    }
}
