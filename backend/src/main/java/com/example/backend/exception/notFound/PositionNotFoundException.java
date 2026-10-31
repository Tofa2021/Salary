package com.example.backend.exception.notFound;

public class PositionNotFoundException extends NotFoundException {
    public PositionNotFoundException(Long id) {
        super("Position", "id", String.valueOf(id));
    }
}
