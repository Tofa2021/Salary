package com.example.backend.auth.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidRefreshTokenException extends InvalidStateException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
