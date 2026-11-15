package com.example.backend.payroll.domain.base.exception;

import com.example.backend.shared.exception.NotFoundException;

import java.util.Map;

public class WorkNormNotFoundException extends NotFoundException {
    public WorkNormNotFoundException(int year, int month) {
        super("WorkNorm", Map.of(
                "year", String.valueOf(year),
                "month", String.valueOf(month)
        ));
    }
}
