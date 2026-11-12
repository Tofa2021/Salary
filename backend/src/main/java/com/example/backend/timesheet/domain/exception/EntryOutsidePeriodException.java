package com.example.backend.timesheet.domain.exception;

import com.example.backend.shared.exception.InvalidStateException;

import java.time.LocalDate;

public class EntryOutsidePeriodException extends InvalidStateException {
    public EntryOutsidePeriodException(LocalDate date, LocalDate periodStart, LocalDate periodEnd) {
        super("Date = " + date + " is out of period from " + periodStart + " -> " + periodEnd);
    }
}
