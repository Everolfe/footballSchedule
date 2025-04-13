package com.github.everolfe.footballmatches.exceptions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class ValidationUtils {

    private ValidationUtils() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    public static void validateProperName(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) {
            throw new InvalidProperNameException(cityName);
        }

        if (!Character.isUpperCase(cityName.charAt(0))) {
            throw new InvalidProperNameException(cityName);
        }

        if (!cityName.matches("[A-Z][a-zA-Z\\s-]+")) {
            throw new InvalidProperNameException(cityName);
        }
    }

    public static void validateNonNegative(String fieldName, Number value) {

        if (value instanceof Integer integer && integer < 0) {
            throw new NegativeNumberException(fieldName, value);
        }

        if (value instanceof Double doubleValue && doubleValue < 0) {
            throw new NegativeNumberException(fieldName, value);
        }

        if (value instanceof Long longValue && longValue < 0) {
            throw new NegativeNumberException(fieldName, value);
        }

        if (value instanceof Float floatValue && floatValue < 0) {
            throw new NegativeNumberException(fieldName, value);
        }

        if (value instanceof Short shortValue && shortValue < 0) {
            throw new NegativeNumberException(fieldName, value);
        }
    }

    public static void validateCapitalizedWords(String fieldName, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidNameFormatException(fieldName, value);
        }

        String[] words = value.split("\\s+");
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (!Character.isUpperCase(word.charAt(0))) {
                throw new InvalidNameFormatException(fieldName, value);
            }
        }
    }

    public static void validateDateFormat(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new InvalidDateException(dateString);
        }
        try {
            LocalDateTime.parse(dateString);
        } catch (DateTimeParseException e) {
            throw new InvalidDateException(dateString);
        }
    }

    public static void validateFutureDate(String dateString) {
        LocalDate date = LocalDate.parse(dateString);
        if (date.isBefore(LocalDate.now())) {
            throw new InvalidDateException(dateString + " (date must be in future)");
        }
    }
}