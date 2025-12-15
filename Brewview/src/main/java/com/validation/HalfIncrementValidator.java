package com.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class HalfIncrementValidator implements ConstraintValidator<ValidHalfIncrement, BigDecimal> {

    private static final BigDecimal INCREMENT = new BigDecimal("0.5");
    private static final BigDecimal MIN_VALUE = new BigDecimal("0.0");
    private static final BigDecimal MAX_VALUE = new BigDecimal("5.0");

    @Override
    public void initialize(ValidHalfIncrement constraintAnnotation) {}

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null validation
        }

        // Check range
        if (value.compareTo(MIN_VALUE) < 0 || value.compareTo(MAX_VALUE) > 0) {
            return false;
        }

        // Check if divisible by 0.5
        // Using remainder: if remainder is 0, it's divisible by 0.5
        BigDecimal remainder = value.remainder(INCREMENT);
        
        // Compare remainder to 0 (accounting for precision)
        return remainder.compareTo(BigDecimal.ZERO) == 0;
    }
}

