package edu.neu.csye6200.tripsync.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsoDateValidator implements ConstraintValidator<IsoDate, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || value.isBlank() || IsoDates.parse(value) != null;
    }
}
