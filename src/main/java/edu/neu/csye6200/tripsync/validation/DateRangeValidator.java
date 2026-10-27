package edu.neu.csye6200.tripsync.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {

    private String startField;
    private String endField;

    @Override
    public void initialize(DateRange constraint) {
        this.startField = constraint.start();
        this.endField = constraint.end();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        BeanWrapperImpl bean = new BeanWrapperImpl(value);
        LocalDate start = parse(bean.getPropertyValue(startField));
        LocalDate end = parse(bean.getPropertyValue(endField));
        if (start == null || end == null || !end.isBefore(start)) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(endField)
                .addConstraintViolation();
        return false;
    }

    private static LocalDate parse(Object value) {
        return value instanceof String text ? IsoDates.parse(text) : null;
    }
}
