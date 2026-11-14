package com.example.backend.payroll.domain.employer.exception;

import com.example.backend.shared.exception.NotFoundException;

public class EmployerContributionNotFoundException extends NotFoundException {
    public EmployerContributionNotFoundException(Long id) {
        super("EmployerContribution", "id", String.valueOf(id));
    }
}
