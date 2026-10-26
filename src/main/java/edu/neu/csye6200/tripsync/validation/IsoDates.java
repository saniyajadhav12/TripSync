package edu.neu.csye6200.tripsync.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

final class IsoDates {

    // "uuuu" with STRICT resolving rejects dates such as 2026-02-30
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);

    private IsoDates() {
    }

    // Returns the parsed date, or null if the value is not a valid yyyy-MM-dd date
    static LocalDate parse(String value) {
        try {
            return LocalDate.parse(value, FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
