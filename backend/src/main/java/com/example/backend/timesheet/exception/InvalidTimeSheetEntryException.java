package com.example.backend.timesheet.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidTimeSheetEntryException extends InvalidStateException {
    public InvalidTimeSheetEntryException(String message) {
        super(message);
    }
}
