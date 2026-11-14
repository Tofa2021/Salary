package com.example.backend.payroll.domain.shared.model;


import com.example.backend.payroll.domain.shared.exception.InvalidDateRangeException;

import java.time.LocalDate;

public record DateRange(LocalDate from, LocalDate to) {

    public DateRange {
        if (from == null) {
            throw new InvalidDateRangeException("from is required");
        }
        if (to == null) {
            throw new InvalidDateRangeException("to is required");
        }
        if (to.isBefore(from)) {
            throw new InvalidDateRangeException("to >= from");
        }
    }

    public static DateRange of(LocalDate from, LocalDate to) {
        return new DateRange(from, to);
    }

    public boolean contains(LocalDate date) {
        if (date == null) {
            return false;
        }
        return !date.isBefore(from) && !date.isAfter(to);
    }

    public boolean overlaps(DateRange other) {
        if (other == null) {
            return false;
        }
        return !to.isBefore(other.from) && !other.to.isBefore(from);
    }
}
