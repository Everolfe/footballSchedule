package com.github.everolfe.footballmatches.exceptions;

public class NegativeNumberException extends BadRequestException {
    public NegativeNumberException(String fieldName, Number value) {
        super("Field '" + fieldName + "' cannot be negative. Received: " + value);
    }
}