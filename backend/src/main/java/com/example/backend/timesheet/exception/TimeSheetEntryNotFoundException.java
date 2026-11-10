package com.example.backend.timesheet.exception;

import com.example.backend.shared.exception.NotFoundException;

import java.util.Map;

public class TimeSheetEntryNotFoundException extends NotFoundException {
    public TimeSheetEntryNotFoundException(Long id) {
        super("TimeSheetEntry", "id", String.valueOf(id));
    }

    public TimeSheetEntryNotFoundException(Long timeSheetId, Long employeeId) {
        super("TimeSheetEntry", Map.of("timeSheetId", String.valueOf(timeSheetId), "employeeId", String.valueOf(employeeId)));
    }
}
