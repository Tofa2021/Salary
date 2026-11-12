package com.example.backend.employee.api;

import com.example.backend.employee.api.dto.EmploymentContractResult;

import java.util.Optional;

public interface EmploymentContractLookup {
    Optional<EmploymentContractResult> findById(Long id);
}
