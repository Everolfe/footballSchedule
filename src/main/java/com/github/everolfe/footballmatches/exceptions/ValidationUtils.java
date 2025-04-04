package com.github.everolfe.footballmatches.exceptions;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ValidationUtils {

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
        if (value == null) {
            throw new NegativeNumberException(fieldName, null);
        }

        if (value instanceof Integer && (Integer) value < 0) {
            throw new NegativeNumberException(fieldName, value);
        }

        if (value instanceof Double && (Double) value < 0) {
            throw new NegativeNumberException(fieldName, value);
        }

        if (value instanceof Long && (Long) value < 0) {
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

        if (!dateString.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new InvalidDateException(dateString);
        }

        try {
            LocalDate date = LocalDate.parse(dateString);
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