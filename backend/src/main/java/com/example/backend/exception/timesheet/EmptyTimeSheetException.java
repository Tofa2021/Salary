package com.example.backend.exception.timesheet;

import com.example.backend.exception.InvalidStateException;

public class EmptyTimeSheetException extends InvalidStateException {
    public EmptyTimeSheetException(Long id) {
        super("Time sheet (id = " + id + ") cannot be empty");
    }
}
