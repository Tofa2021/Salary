package com.example.backend.timesheet.domain.exception;

import com.example.backend.shared.exception.InvalidStateException;
import com.example.backend.timesheet.domain.model.TimeSheetStatus;

public class InvalidTimeSheetStatusException extends InvalidStateException {
    public InvalidTimeSheetStatusException(Long id, TimeSheetStatus currentStatus, TimeSheetStatus to) {
        super("Time sheet id = " + id + " with status = " + currentStatus + " cannot set " + to);
    }
}
