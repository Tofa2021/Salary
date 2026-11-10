package com.example.backend.employee.domain.exception.employmentcontract;

import com.example.backend.shared.exception.AlreadyExistsException;

public class EmploymentContractNumberAlreadyExistsException extends AlreadyExistsException {
    public EmploymentContractNumberAlreadyExistsException(String number) {
        super("EmploymentContract", "contractNumber", number);
    }
}
