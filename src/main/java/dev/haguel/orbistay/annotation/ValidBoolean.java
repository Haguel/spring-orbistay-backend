package dev.haguel.orbistay.annotation;

import dev.haguel.orbistay.annotation.validator.BooleanValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BooleanValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBoolean {
    String message() default "Invalid boolean value, expected 'true' or 'false'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
