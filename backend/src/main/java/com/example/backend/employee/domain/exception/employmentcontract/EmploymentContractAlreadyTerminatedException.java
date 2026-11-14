package com.example.backend.employee.domain.exception.employmentcontract;

import com.example.backend.shared.exception.ConflictException;

public class EmploymentContractAlreadyTerminatedException extends ConflictException {
    public EmploymentContractAlreadyTerminatedException(Long id) {
        super("Contract " + id + " is already terminated");
    }
}
