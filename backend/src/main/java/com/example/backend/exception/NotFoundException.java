package com.example.backend.exception;

import java.util.Map;
import java.util.stream.Collectors;

public abstract class NotFoundException extends ApplicationException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String resourceName, String idName, String id) {
        super(resourceName + " not found with " + idName + " = " + id);
    }

    public NotFoundException(String resourceName, Map<String, String> params) {
        super(resourceName + " not found with " +
                params.entrySet().stream()
                        .map(param -> param.getKey() + " = " + param.getValue())
                        .collect(Collectors.joining(", ")));
    }
}
