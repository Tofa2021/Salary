package com.example.backend.employee.domain.exception.employmentcontract;

import com.example.backend.shared.exception.InvalidStateException;

import java.time.LocalDate;

public class InvalidEmploymentContractPeriodException extends InvalidStateException {
    public InvalidEmploymentContractPeriodException(LocalDate start, LocalDate end) {
        super("endDate " + end + " cannot be before startDate " + start);
    }
}
