package com.example.backend.exception.employmentcontract;

import com.example.backend.exception.NotFoundException;

public class EmploymentContractNotFoundException extends NotFoundException {
    public EmploymentContractNotFoundException(String message) {
        super(message);
    }

    public EmploymentContractNotFoundException(Long id) {
        super("EmploymentContract", "id", String.valueOf(id));
    }
}
