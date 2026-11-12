package com.example.backend.timesheet.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class TimeSheetEntry {
    private Long id;
    private LocalDate date;
    private WorkHours hours;
    private TimeSheetEntryType type;

    public static TimeSheetEntry create(LocalDate date, WorkHours hours, TimeSheetEntryType type) {
        if (date == null) {
            throw new IllegalArgumentException("date is required");
        }

        if (hours == null) {
            throw new IllegalArgumentException("hours is required");
        }

        if (hours.value().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("hours must be > 0");
        }

        if (type == null) {
            throw new IllegalArgumentException("type is required");
        }

        TimeSheetEntry entry = new TimeSheetEntry();
        entry.date = date;
        entry.hours = hours;
        entry.type = type;
        return entry;
    }

    public static TimeSheetEntry rehydrate(Long id, LocalDate date, WorkHours hours, TimeSheetEntryType type) {
        TimeSheetEntry entry = new TimeSheetEntry();
        entry.id = id;
        entry.date = date;
        entry.hours = hours;
        entry.type = type;
        return entry;
    }

    public void changeHours(WorkHours newHours) {
        if (newHours == null) {
            throw new IllegalArgumentException("newHours is required");
        }
        this.hours = newHours;
    }

    public void changeType(TimeSheetEntryType newType) {
        if (newType == null) {
            throw new IllegalArgumentException("newType is required");
        }
        this.type = newType;
    }
}
