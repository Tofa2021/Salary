package com.example.backend.employee.domain.exception.employmentcontract;

import com.example.backend.shared.exception.InvalidStateException;

public class ExtendEmploymentContractException extends InvalidStateException {
    public ExtendEmploymentContractException(String message) {
        super(message);
    }
}
