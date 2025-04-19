package com.github.everolfe.footballmatches.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Handler {
    private Handler() {}

    public static  <T> ResponseEntity<T> handleResponse(final T body, final boolean condition) {
        return condition
                ? ResponseEntity.ok(body)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
