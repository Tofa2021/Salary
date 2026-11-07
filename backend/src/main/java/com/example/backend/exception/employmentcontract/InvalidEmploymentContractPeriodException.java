package com.example.backend.exception.employmentcontract;

import com.example.backend.exception.InvalidStateException;

import java.time.LocalDate;

public class InvalidEmploymentContractPeriodException extends InvalidStateException {
    public InvalidEmploymentContractPeriodException(LocalDate start, LocalDate end) {
        super("endDate " + end + " cannot be before startDate " + start);
    }
}
