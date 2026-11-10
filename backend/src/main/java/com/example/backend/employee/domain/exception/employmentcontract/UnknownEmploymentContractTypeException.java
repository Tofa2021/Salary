package com.example.backend.employee.domain.exception.employmentcontract;

import com.example.backend.shared.exception.InvalidStateException;

public class UnknownEmploymentContractTypeException extends InvalidStateException {
    public UnknownEmploymentContractTypeException(String value) {
        super("Unknown contractType: " + value);
    }
}
