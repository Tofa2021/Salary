package com.example.backend.exception.employmentcontract;

import com.example.backend.exception.AlreadyExistsException;

public class EmploymentContractNumberAlreadyExistsException extends AlreadyExistsException {
    public EmploymentContractNumberAlreadyExistsException(String number) {
        super("EmploymentContract", "contractNumber", number);
    }
}
