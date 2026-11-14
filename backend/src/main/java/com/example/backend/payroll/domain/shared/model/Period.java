package com.example.backend.payroll.domain.shared.model;


import com.example.backend.payroll.domain.shared.exception.InvalidPeriodException;

import java.time.LocalDate;
import java.time.YearMonth;

public record Period(int year, int month) {

    public Period {
        if (month < 1 || month > 12) {
            throw new InvalidPeriodException("month must be 1..12");
        }
        if (year < 1900 || year > 9999) {
            throw new InvalidPeriodException("year out of range");
        }
    }

    public static Period of(int year, int month) {
        return new Period(year, month);
    }

    public LocalDate start() {
        return YearMonth.of(year, month).atDay(1);
    }

    public LocalDate end() {
        return YearMonth.of(year, month).atEndOfMonth();
    }

    public DateRange dateRange() {
        return new DateRange(start(), end());
    }

    public boolean contains(LocalDate date) {
        return dateRange().contains(date);
    }
}
