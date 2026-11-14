package com.example.backend.timesheet.domain.exception;

import com.example.backend.shared.exception.InvalidStateException;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DayHoursExceededException extends InvalidStateException {
    public DayHoursExceededException(LocalDate date, BigDecimal hours) {
        super("Work hours is higher that " + hours + " on date = " + date);
    }
}
