package com.example.backend.exception.employmentcontract;

import com.example.backend.exception.ConflictException;

public class MainJobAlreadyExistsException extends ConflictException {
    public MainJobAlreadyExistsException(Long employeeId, Long existingContractId) {
        super("Employee " + employeeId + " already has main job contract " + existingContractId);
    }
}
