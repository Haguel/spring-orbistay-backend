package dev.haguel.orbistay.annotation.validator;

import dev.haguel.orbistay.annotation.ValidBoolean;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BooleanValidator implements ConstraintValidator<ValidBoolean, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
    }
}
