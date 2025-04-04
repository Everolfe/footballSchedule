package com.github.everolfe.footballmatches.exceptions;

import com.github.everolfe.footballmatches.aspect.AspectAnnotation;
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
    @AspectAnnotation
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

    @AspectAnnotation
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorMessage> handleBadRequestException(BadRequestException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                null);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @AspectAnnotation
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                ex.getMessage(),
                null);
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @AspectAnnotation
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

    @AspectAnnotation
    @ExceptionHandler(InvalidProperNameException.class)
    public ResponseEntity<ErrorMessage> handleInvalidCityNameException(
            InvalidProperNameException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                "City name must start with capital letter and contain only letters");
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @AspectAnnotation
    @ExceptionHandler(NegativeNumberException.class)
    public ResponseEntity<ErrorMessage> handleNegativeNumberException(NegativeNumberException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                "Numeric fields cannot have negative values");
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @AspectAnnotation
    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<ErrorMessage> handleInvalidDateException(InvalidDateException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                "Date must be in format yyyy-MM-dd and be a valid calendar date");
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

}
