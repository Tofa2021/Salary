package com.example.backend.timesheet.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class EmptyTimeSheetException extends InvalidStateException {
    public EmptyTimeSheetException(Long id) {
        super("Time sheet (id = " + id + ") cannot be empty");
    }
}
