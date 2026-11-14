package com.example.backend.auth.domain.exception;

public class TokenRevokedException extends RuntimeException {
    public TokenRevokedException() {
        super("Token revoked");
    }
}
