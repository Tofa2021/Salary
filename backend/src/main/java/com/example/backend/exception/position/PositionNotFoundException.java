package com.example.backend.exception.position;

import com.example.backend.exception.NotFoundException;

public class PositionNotFoundException extends NotFoundException {
    public PositionNotFoundException(Long id) {
        super("Position", "id", String.valueOf(id));
    }
}
