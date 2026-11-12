package com.example.backend.timesheet.domain.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class TimeSheetInvalidDepartmentException extends InvalidStateException {
    public TimeSheetInvalidDepartmentException(Long departmentId) {
        super("Department not found with id = " + departmentId);
    }
}
