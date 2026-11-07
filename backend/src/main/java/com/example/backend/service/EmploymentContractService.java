package com.example.backend.service;

import com.example.backend.dto.employmentcontract.CreateEmploymentContractRequest;
import com.example.backend.dto.employmentcontract.EmploymentContractResult;
import com.example.backend.dto.employmentcontract.UpdateEmploymentContractRequest;
import com.example.backend.exception.employee.EmployeeNotFoundException;
import com.example.backend.exception.employmentcontract.*;
import com.example.backend.model.employee.ContractType;
import com.example.backend.model.employee.Employee;
import com.example.backend.model.employee.EmploymentContract;
import com.example.backend.repository.EmployeeRepository;
import com.example.backend.repository.EmploymentContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmploymentContractService {
    private final EmploymentContractRepository employmentContractRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public EmploymentContractResult create(Long employeeId, CreateEmploymentContractRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        if (employmentContractRepository.existsByContractNumber(request.contractNumber())) {
            throw new EmploymentContractNumberAlreadyExistsException(request.contractNumber());
        }

        if (request.endDate() != null && request.endDate().isBefore(request.startDate())) {
            throw new InvalidEmploymentContractPeriodException(request.startDate(), request.endDate());
        }

        ContractType type = parseContractType(request.contractType());

        if (request.mainJob()) {
            employmentContractRepository.findActiveMainOnDate(employeeId, request.startDate())
                    .ifPresent(existing -> {
                        if (existing.getStartDate().isBefore(request.startDate())
                                || existing.getEndDate() == null
                                || existing.getEndDate().isAfter(request.startDate())
                                || existing.getStartDate().equals(request.startDate())) {
                            throw new MainJobAlreadyExistsException(employeeId, existing.getId());
                        }
                    });
        }

        EmploymentContract contract = new EmploymentContract(
                employee,
                request.contractNumber(),
                request.startDate(),
                request.endDate(),
                type,
                request.baseSalary(),
                request.hourlyRate(),
                request.bonusPercent(),
                request.workHoursPerWeek(),
                request.mainJob()
        );

        return EmploymentContractResult.from(employmentContractRepository.save(contract), LocalDate.now());
    }

    @Transactional(readOnly = true)
    public EmploymentContractResult getById(Long id) {
        return EmploymentContractResult.from(findById(id), LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<EmploymentContractResult> findByEmployee(Long employeeId) {
        LocalDate today = LocalDate.now();
        return employmentContractRepository.findByEmployeeId(employeeId).stream()
                .map(c -> EmploymentContractResult.from(c, today))
                .toList();
    }

    @Transactional(readOnly = true)
    public EmploymentContractResult findActiveMainOnDate(Long employeeId, LocalDate date) {
        EmploymentContract contract = employmentContractRepository.findActiveMainOnDate(employeeId, date)
                .orElseThrow(() -> new EmploymentContractNotFoundException("No active main contract for employee " + employeeId + " on " + date));
        return EmploymentContractResult.from(contract, date);
    }

    @Transactional
    public EmploymentContractResult update(Long id, UpdateEmploymentContractRequest request) {
        EmploymentContract contract = findById(id);
        if (request.baseSalary() != null) {
            contract.setBaseSalary(request.baseSalary());
        }
        if (request.hourlyRate() != null) {
            contract.setHourlyRate(request.hourlyRate());
        }
        if (request.bonusPercent() != null) {
            contract.setBonusPercent(request.bonusPercent());
        }
        if (request.workHoursPerWeek() != null) {
            contract.setWorkHoursPerWeek(request.workHoursPerWeek());
        }
        if (request.endDate() != null) {
            if (request.endDate().isBefore(contract.getStartDate())) {
                throw new InvalidEmploymentContractPeriodException(contract.getStartDate(), request.endDate());
            }
            contract.setEndDate(request.endDate());
        }
        return EmploymentContractResult.from(contract, LocalDate.now());
    }

    @Transactional
    public EmploymentContractResult terminate(Long id, LocalDate terminationDate) {
        EmploymentContract contract = findById(id);
        if (contract.getEndDate() != null && !contract.getEndDate().isAfter(LocalDate.now())) {
            throw new EmploymentContractAlreadyTerminatedException(id);
        }
        if (terminationDate.isBefore(contract.getStartDate())) {
            throw new InvalidEmploymentContractPeriodException(contract.getStartDate(), terminationDate);
        }
        contract.setEndDate(terminationDate);
        return EmploymentContractResult.from(contract, LocalDate.now());
    }

    @Transactional
    public EmploymentContractResult extend(Long id, LocalDate newEndDate) {
        EmploymentContract contract = findById(id);
        if (contract.getContractType() == ContractType.INDEFINITE) {
            throw new ExtendEmploymentContractException("Cannot extend indefinite contract");
        }
        if (newEndDate.isBefore(contract.getStartDate())) {
            throw new InvalidEmploymentContractPeriodException(contract.getStartDate(), newEndDate);
        }
        contract.setEndDate(newEndDate);
        return EmploymentContractResult.from(contract, LocalDate.now());
    }

    @Transactional
    public void delete(Long id) {
        EmploymentContract contract = findById(id);
        employmentContractRepository.delete(contract);
    }

    @Transactional(readOnly = true)
    public List<EmploymentContractResult> findExpiring(int days) {
        LocalDate today = LocalDate.now();
        LocalDate to = today.plusDays(days);
        return employmentContractRepository.findExpiring(ContractType.FIXED_TERM, today, to).stream()
                .map(c -> EmploymentContractResult.from(c, today))
                .toList();
    }

    private ContractType parseContractType(String value) {
        if (value == null) {
            throw new UnknownEmploymentContractTypeException(null);
        }

        try {
            return ContractType.valueOf(value);
        } catch (IllegalArgumentException ex) {
            throw new UnknownEmploymentContractTypeException(value);
        }
    }

    private EmploymentContract findById(Long id) {
        return employmentContractRepository.findById(id)
                .orElseThrow(() -> new EmploymentContractNotFoundException(id));
    }
}
