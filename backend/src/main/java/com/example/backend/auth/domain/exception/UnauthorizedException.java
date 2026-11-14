package com.example.backend.auth.domain.exception;

import com.example.backend.shared.exception.ApplicationException;

public class UnauthorizedException extends ApplicationException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
