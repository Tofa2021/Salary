package com.example.backend.timesheet.domain.exception;

import com.example.backend.shared.exception.ConflictException;

public class EmptyTimeSheetException extends ConflictException {
    public EmptyTimeSheetException(Long id) {
        super("Time sheet (id = " + id + ") cannot be empty");
    }
}
