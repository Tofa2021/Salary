package com.example.backend.exception.notFound;

import com.example.backend.exception.BusinessException;

public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String resourceName, String idName, String id) {
        super(resourceName + " not found with " + idName + " = " + id);
    }
}
