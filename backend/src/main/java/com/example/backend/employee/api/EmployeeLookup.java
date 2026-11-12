package com.example.backend.employee.api;

import com.example.backend.employee.api.dto.EmployeeResult;

import java.util.Optional;

public interface EmployeeLookup {
    Optional<EmployeeResult> findById(Long id);

    boolean existsByDepartmentId(Long departmentId);
}
