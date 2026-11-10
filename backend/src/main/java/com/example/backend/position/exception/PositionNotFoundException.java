package com.example.backend.position.exception;

import com.example.backend.shared.exception.NotFoundException;

public class PositionNotFoundException extends NotFoundException {
    public PositionNotFoundException(Long id) {
        super("Position", "id", String.valueOf(id));
    }
}
