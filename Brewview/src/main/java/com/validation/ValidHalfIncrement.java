package com.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HalfIncrementValidator.class)
@Documented
public @interface ValidHalfIncrement {
    String message() default "Rating must be between 0.0 and 5.0 with 0.5 increments";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

