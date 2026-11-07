package com.example.backend.exception.position;

import com.example.backend.exception.AlreadyExistsException;

public class PositionNameAlreadyExistsException extends AlreadyExistsException {
    public PositionNameAlreadyExistsException(String name) {
        super("Position", "name", name);
    }
}
