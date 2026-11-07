package com.example.backend.exception;

public class AlreadyExistsException extends ConflictException {
    public AlreadyExistsException(String message) {
        super(message);
    }

    public AlreadyExistsException(String resourceName, String idName, String id) {
        super(resourceName + " already exists with " + idName + " = " + id);
    }
}
