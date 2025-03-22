package com.github.everolfe.footballmatches.exceptions;

import java.util.Date;

public record ErrorMessage(int statusCode, Date timestamp, String message, String details) {
}
