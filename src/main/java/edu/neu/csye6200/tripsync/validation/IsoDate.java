package edu.neu.csye6200.tripsync.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// A real calendar date in yyyy-MM-dd format. Null or blank values are left to @NotBlank.
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsoDateValidator.class)
public @interface IsoDate {
    String message() default "must be a date in yyyy-MM-dd format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
