package com.example.backend.position.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidPositionNameException extends InvalidStateException {
    public InvalidPositionNameException(String name) {
        super("Position name cannot be blank");
    }
}
