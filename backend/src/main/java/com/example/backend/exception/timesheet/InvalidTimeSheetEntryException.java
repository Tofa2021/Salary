package com.example.backend.exception.timesheet;

import com.example.backend.exception.InvalidStateException;

public class InvalidTimeSheetEntryException extends InvalidStateException {
    public InvalidTimeSheetEntryException(String message) {
        super(message);
    }
}
