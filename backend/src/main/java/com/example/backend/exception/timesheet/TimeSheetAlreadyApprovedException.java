package com.example.backend.exception.timesheet;

import com.example.backend.exception.ConflictException;

public class TimeSheetAlreadyApprovedException extends ConflictException {
    public TimeSheetAlreadyApprovedException(Long timeSheetId) {
        super("Time sheet already approved with id = " + timeSheetId);
    }
}
