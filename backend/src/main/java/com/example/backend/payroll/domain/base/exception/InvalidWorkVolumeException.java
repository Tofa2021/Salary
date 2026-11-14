package com.example.backend.payroll.domain.base.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidWorkVolumeException extends InvalidStateException {
    public InvalidWorkVolumeException(String message) {
        super(message);
    }
}
