package com.github.everolfe.footballmatches.exceptions;

public class InvalidDateException extends BadRequestException {
    public InvalidDateException(String dateString) {
        super("Invalid date format or value. Expected format: yyyy-MM-dd. Received: '"
                + dateString + "'");
    }
}