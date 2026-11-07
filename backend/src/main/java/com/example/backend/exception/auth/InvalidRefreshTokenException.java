package com.example.backend.exception.auth;

import com.example.backend.exception.InvalidStateException;

public class InvalidRefreshTokenException extends InvalidStateException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
