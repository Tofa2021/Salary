package com.example.backend.timesheet.domain.policy;

import com.example.backend.timesheet.domain.model.TimeSheetEntryType;
import com.example.backend.timesheet.domain.model.WorkHours;

import java.time.LocalDate;

public record HoursPartition(
        LocalDate date,
        WorkHours hours,
        TimeSheetEntryType type
) {
    public HoursPartition {
        if (date == null) {
            throw new IllegalArgumentException("date is required");
        }
        
        if (hours == null) {
            throw new IllegalArgumentException("hours is required");
        }

        if (type == null) {
            throw new IllegalArgumentException("type is required");
        }
    }
}
