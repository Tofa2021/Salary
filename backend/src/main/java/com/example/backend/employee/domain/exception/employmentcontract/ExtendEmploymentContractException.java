package com.example.backend.employee.domain.exception.employmentcontract;

import com.example.backend.shared.exception.ConflictException;

public class ExtendEmploymentContractException extends ConflictException {
    public ExtendEmploymentContractException(String message) {
        super(message);
    }
}
