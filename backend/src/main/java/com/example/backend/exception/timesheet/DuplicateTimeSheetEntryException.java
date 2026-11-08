package com.example.backend.exception.timesheet;

import com.example.backend.exception.ConflictException;

public class DuplicateTimeSheetEntryException extends ConflictException {
    public DuplicateTimeSheetEntryException(Long timeSheetId, Long employeeId) {
        super("Employee with id = " + employeeId + " already in time sheet with id = " + timeSheetId);
    }
}
