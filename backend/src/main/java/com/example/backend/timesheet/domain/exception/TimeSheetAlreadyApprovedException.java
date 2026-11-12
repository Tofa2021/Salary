package com.example.backend.timesheet.domain.exception;

import com.example.backend.shared.exception.ConflictException;

public class TimeSheetAlreadyApprovedException extends ConflictException {
    public TimeSheetAlreadyApprovedException(Long timeSheetId) {
        super("Time sheet already approved with id = " + timeSheetId);
    }
}
