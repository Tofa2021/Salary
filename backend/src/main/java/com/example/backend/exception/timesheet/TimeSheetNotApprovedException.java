package com.example.backend.exception.timesheet;

import com.example.backend.exception.ConflictException;

public class TimeSheetNotApprovedException extends ConflictException {
    public TimeSheetNotApprovedException(Long id) {
        super("Time sheet (id=" + id + ") not approved");
    }
}
