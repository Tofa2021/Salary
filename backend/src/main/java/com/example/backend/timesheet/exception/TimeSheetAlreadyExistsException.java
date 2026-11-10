package com.example.backend.timesheet.exception;

import com.example.backend.shared.exception.AlreadyExistsException;

public class TimeSheetAlreadyExistsException extends AlreadyExistsException {
    public TimeSheetAlreadyExistsException(Long departmentId, int year, int month) {
        super("Time sheet already exists with department = " + departmentId + " year = " + year + " month = " + month);
    }
}
