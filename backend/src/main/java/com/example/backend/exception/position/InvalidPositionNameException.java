package com.example.backend.exception.position;

import com.example.backend.exception.InvalidStateException;

public class InvalidPositionNameException extends InvalidStateException {
    public InvalidPositionNameException(String name) {
        super("Position name cannot be blank");
    }
}
