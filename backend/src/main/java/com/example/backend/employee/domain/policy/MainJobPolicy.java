package com.example.backend.employee.domain.policy;

import com.example.backend.employee.domain.exception.employmentcontract.MainJobAlreadyExistsException;
import com.example.backend.employee.domain.model.EmploymentContract;

import java.util.Optional;

public class MainJobPolicy {
    public void ensureNoOverlappingMainJob(
            boolean isMainJob,
            Optional<EmploymentContract> overlapping
    ) {
        if (!isMainJob) {
            return;
        }
        
        overlapping.ifPresent(existing -> {
            throw new MainJobAlreadyExistsException(existing.getEmployeeId(), existing.getId());
        });
    }
}
