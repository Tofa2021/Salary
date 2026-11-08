package com.example.backend.exception.timesheet;

import com.example.backend.exception.AlreadyExistsException;

public class TimeSheetAlreadyExistsException extends AlreadyExistsException {
    public TimeSheetAlreadyExistsException(Long departmentId, int year, int month) {
        super("Time sheet already exists with department = " + departmentId + " year = " + year + " month = " + month);
    }
}
