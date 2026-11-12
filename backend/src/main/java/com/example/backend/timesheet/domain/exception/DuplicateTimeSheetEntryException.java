package com.example.backend.timesheet.domain.exception;

import com.example.backend.shared.exception.ConflictException;

public class DuplicateTimeSheetEntryException extends ConflictException {
    public DuplicateTimeSheetEntryException(Long timeSheetId, Long employeeId) {
        super("Employee with id = " + employeeId + " already in time sheet with id = " + timeSheetId);
    }
}
