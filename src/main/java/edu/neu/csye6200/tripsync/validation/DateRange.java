package edu.neu.csye6200.tripsync.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Class-level check that the end date is not before the start date. Both are yyyy-MM-dd strings.
// The error is reported on the end field. Missing or malformed dates are left to the field constraints.
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface DateRange {
    String start();
    String end();
    String message() default "must not be before the start date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
