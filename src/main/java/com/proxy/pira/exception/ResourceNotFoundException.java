package com.proxy.pira.exception;

/**
 * Thrown when a requested resource (project or ticket) cannot be found in the database.
 * Mapped to HTTP 404 by {@link com.proxy.pira.controller.ControllerAdvice}.
 */
public class ResourceNotFoundException extends RuntimeException {

    /** @param message human-readable description of the missing resource */
    public ResourceNotFoundException(String message) {
        super(message);
    }

}
