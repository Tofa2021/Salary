package com.example.backend.auth.domain.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidRefreshTokenException extends InvalidStateException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
