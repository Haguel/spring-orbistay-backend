package dev.haguel.orbistay.annotation.validator;

import dev.haguel.orbistay.annotation.ValidDateFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateFormatValidator implements ConstraintValidator<ValidDateFormat, String> {

    private static final String DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";

    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }
        return date.matches(DATE_PATTERN);
    }
}
