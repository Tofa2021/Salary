package com.example.backend.employee.domain.port;

import com.example.backend.employee.domain.model.ContractType;
import com.example.backend.employee.domain.model.EmploymentContract;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmploymentContractRepository {
    boolean existsByContractNumber(String contractNumber);

    Optional<EmploymentContract> findActiveMainOnDate(Long employeeId, LocalDate localDate);

    EmploymentContract save(EmploymentContract contract);

    List<EmploymentContract> findByEmployeeId(Long employeeId);

    void delete(EmploymentContract contract);

    List<EmploymentContract> findExpiring(ContractType contractType, LocalDate today, LocalDate to);

    Optional<EmploymentContract> findById(Long id);
}
