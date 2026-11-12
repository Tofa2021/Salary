package com.example.backend.employee.api;

import com.example.backend.employee.api.dto.CreateEmploymentContractCommand;
import com.example.backend.employee.api.dto.EmploymentContractResult;
import com.example.backend.employee.api.dto.UpdateEmploymentContractCommand;

import java.time.LocalDate;
import java.util.List;

public interface EmploymentContractFacade {
    EmploymentContractResult create(Long employeeId, CreateEmploymentContractCommand request);

    EmploymentContractResult getById(Long id);

    List<EmploymentContractResult> findByEmployee(Long employeeId);

    EmploymentContractResult findActiveMainOnDate(Long employeeId, LocalDate date);

    EmploymentContractResult update(Long id, UpdateEmploymentContractCommand request);

    EmploymentContractResult terminate(Long id, LocalDate terminationDate);

    EmploymentContractResult extend(Long id, LocalDate newEndDate);

    void delete(Long id);

    List<EmploymentContractResult> findExpiring(int days);
}
