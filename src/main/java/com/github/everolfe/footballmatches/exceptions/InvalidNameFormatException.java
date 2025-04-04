package com.github.everolfe.footballmatches.exceptions;

public class InvalidNameFormatException extends BadRequestException {
    public InvalidNameFormatException(String fieldName, String value) {
        super("Each word in '" + fieldName
                + "' must start with capital letter. Received: '" + value + "'");
    }
}