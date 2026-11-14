package com.example.backend.auth.domain.exception.user;

import com.example.backend.shared.exception.ConflictException;

public class LastAdminException extends ConflictException {
    public LastAdminException(String message) {
        super(message);
    }
}
