package com.example.backend.employee.domain.exception.employmentcontract;

import com.example.backend.shared.exception.InvalidStateException;

public class EmploymentContractAlreadyTerminatedException extends InvalidStateException {
    public EmploymentContractAlreadyTerminatedException(Long id) {
        super("Contract " + id + " is already terminated");
    }
}
