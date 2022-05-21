package com.acme.taskmanager.exception;

/**
 * Exceptions for not found entities.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
