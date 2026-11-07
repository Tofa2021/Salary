package com.example.backend.exception.employmentcontract;

import com.example.backend.exception.InvalidStateException;

public class UnknownEmploymentContractTypeException extends InvalidStateException {
    public UnknownEmploymentContractTypeException(String value) {
        super("Unknown contractType: " + value);
    }
}
