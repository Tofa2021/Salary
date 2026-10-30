package com.example.backend.exception;

public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String resourceName, String idName, String id) {
        super(resourceName + " not found with " + idName + " = " + id);
    }
}
