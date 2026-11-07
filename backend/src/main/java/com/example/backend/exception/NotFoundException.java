package com.example.backend.exception;

public abstract class NotFoundException extends ApplicationException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String resourceName, String idName, String id) {
        super(resourceName + " not found with " + idName + " = " + id);
    }
}
