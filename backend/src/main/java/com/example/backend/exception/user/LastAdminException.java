package com.example.backend.exception.user;

import com.example.backend.exception.ConflictException;

public class LastAdminException extends ConflictException {
    public LastAdminException(String message) {
        super(message);
    }
}
