package com.example.backend.exception.employmentcontract;

import com.example.backend.exception.InvalidStateException;

public class ExtendEmploymentContractException extends InvalidStateException {
    public ExtendEmploymentContractException(String message) {
        super(message);
    }
}
