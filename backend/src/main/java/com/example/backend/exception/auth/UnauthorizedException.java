package com.example.backend.exception.auth;

import com.example.backend.exception.ApplicationException;

public class UnauthorizedException extends ApplicationException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
