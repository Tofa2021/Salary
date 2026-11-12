package com.example.backend.timesheet.domain.exception;

import com.example.backend.shared.exception.InvalidStateException;

import java.time.LocalDate;

public class InvalidTimeSheetPeriodException extends InvalidStateException {
    public InvalidTimeSheetPeriodException(LocalDate periodStart, LocalDate periodEnd) {
        super("Period start = " + periodStart + " cannot be before period end = " + periodEnd);
    }
}
