package com.example.backend.timesheet.domain.exception;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DayHoursExceededException extends RuntimeException {
    public DayHoursExceededException(LocalDate date, BigDecimal hours) {
        super("Work hours is higher that " + hours + " on date = " + date);
    }
}
