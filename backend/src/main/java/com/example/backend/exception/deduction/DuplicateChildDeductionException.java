package com.example.backend.exception.deduction;

import com.example.backend.exception.ConflictException;

public class DuplicateChildDeductionException extends ConflictException {
    public DuplicateChildDeductionException(Long employeeId, String documentNumber) {
        super("Child deduction with document " + documentNumber + " already exists for employee " + employeeId);
    }
}
