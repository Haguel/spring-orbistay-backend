package dev.haguel.orbistay.annotation;

import dev.haguel.orbistay.annotation.validator.IsDateAfterTodayValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IsDateAfterTodayValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface IsDateAfterToday {
    String message() default "must be after today's date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
