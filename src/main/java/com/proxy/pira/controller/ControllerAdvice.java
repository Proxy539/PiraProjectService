package com.proxy.pira.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.proxy.pira.dto.ErrorResponseDTO;
import com.proxy.pira.exception.ResourceNotFoundException;

@RestControllerAdvice
public class ControllerAdvice {

    public static final String PIRA_SERVICE_NAME = "Pira-service";

    @ExceptionHandler(exception = ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFound(ResourceNotFoundException exception) {

        var errorBody = ErrorResponseDTO.builder()
                    .service(PIRA_SERVICE_NAME)
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(exception.getMessage())
                    .build();

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorBody);
    }

}
