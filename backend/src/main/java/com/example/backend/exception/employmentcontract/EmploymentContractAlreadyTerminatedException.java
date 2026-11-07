package com.example.backend.exception.employmentcontract;

import com.example.backend.exception.InvalidStateException;

public class EmploymentContractAlreadyTerminatedException extends InvalidStateException {
    public EmploymentContractAlreadyTerminatedException(Long id) {
        super("Contract " + id + " is already terminated");
    }
}
