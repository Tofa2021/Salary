package com.example.backend.position.exception;

import com.example.backend.shared.exception.AlreadyExistsException;

public class PositionNameAlreadyExistsException extends AlreadyExistsException {
    public PositionNameAlreadyExistsException(String name) {
        super("Position", "name", name);
    }
}
