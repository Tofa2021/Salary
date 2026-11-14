package com.example.backend.payroll.domain.shared.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidMoneyException extends InvalidStateException {
    public InvalidMoneyException(String message) {
        super(message);
    }
}
