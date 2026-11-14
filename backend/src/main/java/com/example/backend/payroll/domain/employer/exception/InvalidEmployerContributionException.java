package com.example.backend.payroll.domain.employer.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidEmployerContributionException extends InvalidStateException {
    public InvalidEmployerContributionException(String message) {
        super(message);
    }
}
