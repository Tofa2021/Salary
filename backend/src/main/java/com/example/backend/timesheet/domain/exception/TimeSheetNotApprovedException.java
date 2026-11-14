package com.example.backend.timesheet.domain.exception;

import com.example.backend.shared.exception.ConflictException;

public class TimeSheetNotApprovedException extends ConflictException {
    public TimeSheetNotApprovedException(Long id) {
        super("Time sheet (id=" + id + ") not approved");
    }
}
