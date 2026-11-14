package com.example.backend.payroll.domain.net.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidNetException extends InvalidStateException {
    public InvalidNetException(String message) {
        super(message);
    }
}
