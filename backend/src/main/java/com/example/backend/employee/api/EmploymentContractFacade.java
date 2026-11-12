package com.example.backend.employee.api;

import com.example.backend.employee.api.dto.EmploymentContractResult;
import com.example.backend.employee.infrastructure.web.dto.CreateEmploymentContractRequest;
import com.example.backend.employee.infrastructure.web.dto.UpdateEmploymentContractRequest;

import java.time.LocalDate;
import java.util.List;

public interface EmploymentContractFacade {
    EmploymentContractResult create(Long employeeId, CreateEmploymentContractRequest request);

    EmploymentContractResult getById(Long id);

    List<EmploymentContractResult> findByEmployee(Long employeeId);

    EmploymentContractResult findActiveMainOnDate(Long employeeId, LocalDate date);

    EmploymentContractResult update(Long id, UpdateEmploymentContractRequest request);

    EmploymentContractResult terminate(Long id, LocalDate terminationDate);

    EmploymentContractResult extend(Long id, LocalDate newEndDate);

    void delete(Long id);

    List<EmploymentContractResult> findExpiring(int days);
}
