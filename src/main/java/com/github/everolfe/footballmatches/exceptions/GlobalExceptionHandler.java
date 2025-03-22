package com.github.everolfe.footballmatches.exceptions;

import com.github.everolfe.footballmatches.aspect.AspectAnnotaion;
import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Component
@ControllerAdvice
public class GlobalExceptionHandler {
    @AspectAnnotaion
    @ExceptionHandler(ResourcesNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleResourcesNotFoundException(
            ResourcesNotFoundException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                ex.getMessage(),
                null);
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @AspectAnnotaion
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorMessage> handleBadRequestException(BadRequestException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                null);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @AspectAnnotaion
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                ex.getMessage(),
                null);
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @AspectAnnotaion
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                null);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
