package dev.haguel.orbistay.annotation;

import dev.haguel.orbistay.annotation.validator.DateFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateFormatValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateFormat {
    String message() default "Invalid date format, expected yyyy-mm-dd";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
