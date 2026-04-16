package com.proxy.pira.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.proxy.pira.dto.ErrorResponseDTO;
import com.proxy.pira.exception.ResourceNotFoundException;

/** Centralises exception-to-HTTP-response mapping for all controllers. */
@RestControllerAdvice
public class ControllerAdvice {

    public static final String PIRA_SERVICE_NAME = "Pira-service";

    @ExceptionHandler(exception = ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFound(ResourceNotFoundException exception) {

        final var errorBody = ErrorResponseDTO.builder()
                    .service(PIRA_SERVICE_NAME)
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(exception.getMessage())
                    .build();

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorBody);
    }

    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException exception) {

        final List<String> errors = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());

        final var errorBody = ErrorResponseDTO.builder()
            .service(PIRA_SERVICE_NAME)
            .status(HttpStatus.BAD_REQUEST.value())
            .message("Validation failed")
            .validationErrors(errors)
            .build();

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorBody);
    }

}
