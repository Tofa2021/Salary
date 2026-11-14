package com.example.backend.auth.domain.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidCredentialsException extends InvalidStateException {
    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}
