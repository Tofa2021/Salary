package com.example.backend.timesheet.domain.exception.hours;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidWorkHoursException extends InvalidStateException {
    public InvalidWorkHoursException(String message) {
        super(message);
    }
}
