package com.example.backend.employee.application.service;

import com.example.backend.employee.api.dto.EmploymentContractResult;
import com.example.backend.employee.domain.exception.employee.EmployeeNotFoundException;
import com.example.backend.employee.domain.exception.employmentcontract.*;
import com.example.backend.employee.domain.model.ContractType;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.domain.model.EmploymentContract;
import com.example.backend.employee.domain.port.EmployeeRepository;
import com.example.backend.employee.domain.port.EmploymentContractRepository;
import com.example.backend.employee.infrastructure.web.dto.CreateEmploymentContractRequest;
import com.example.backend.employee.infrastructure.web.dto.UpdateEmploymentContractRequest;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class EmploymentContractService {
    private final EmploymentContractRepository employmentContractRepository;
    private final EmployeeRepository employeeRepository;
    private final Clock clock;

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

        EmploymentContract contract = new EmploymentContract();
        contract.setEmployeeId(employee.getId());
        contract.setContractNumber(request.contractNumber());
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.endDate());
        contract.setContractType(type);
        contract.setBaseSalary(request.baseSalary());
        contract.setHourlyRate(request.hourlyRate());
        contract.setBonusPercent(request.bonusPercent());
        contract.setWorkHoursPerWeek(request.workHoursPerWeek());
        contract.setMainJob(request.mainJob());

        return toResult(employmentContractRepository.save(contract), today());
    }

    public EmploymentContractResult getById(Long id) {
        return toResult(findById(id), today());
    }

    public List<EmploymentContractResult> findByEmployee(Long employeeId) {
        LocalDate today = today();
        return employmentContractRepository.findByEmployeeId(employeeId).stream()
                .map(c -> toResult(c, today))
                .toList();
    }

    public EmploymentContractResult findActiveMainOnDate(Long employeeId, LocalDate date) {
        EmploymentContract contract = employmentContractRepository.findActiveMainOnDate(employeeId, date)
                .orElseThrow(() -> new EmploymentContractNotFoundException("No active main contract for employee " + employeeId + " on " + date));
        return toResult(contract, date);
    }

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

        employmentContractRepository.save(contract);

        return toResult(contract, today());
    }

    public EmploymentContractResult terminate(Long id, LocalDate terminationDate) {
        EmploymentContract contract = findById(id);
        if (contract.getEndDate() != null && !contract.getEndDate().isAfter(today())) {
            throw new EmploymentContractAlreadyTerminatedException(id);
        }
        if (terminationDate.isBefore(contract.getStartDate())) {
            throw new InvalidEmploymentContractPeriodException(contract.getStartDate(), terminationDate);
        }
        contract.setEndDate(terminationDate);
        employmentContractRepository.save(contract);
        return toResult(contract, today());
    }

    public EmploymentContractResult extend(Long id, LocalDate newEndDate) {
        EmploymentContract contract = findById(id);
        if (contract.getContractType() == ContractType.INDEFINITE) {
            throw new ExtendEmploymentContractException("Cannot extend indefinite contract");
        }
        if (newEndDate.isBefore(contract.getStartDate())) {
            throw new InvalidEmploymentContractPeriodException(contract.getStartDate(), newEndDate);
        }
        contract.setEndDate(newEndDate);
        return toResult(contract, today());
    }

    public void delete(Long id) {
        employmentContractRepository.delete(findById(id));
    }

    public List<EmploymentContractResult> findExpiring(int days) {
        LocalDate today = today();
        LocalDate to = today.plusDays(days);
        return employmentContractRepository.findExpiring(ContractType.FIXED_TERM, today, to).stream()
                .map(c -> toResult(c, today))
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

    private EmploymentContractResult toResult(EmploymentContract contract, LocalDate today) {
        boolean active = !contract.getStartDate().isAfter(today) &&
                (contract.getEndDate() == null || !contract.getEndDate().isBefore(today));
        return new EmploymentContractResult(
                contract.getId(),
                contract.getEmployeeId(),
                contract.getContractNumber(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getContractType(),
                contract.getBaseSalary(),
                contract.getHourlyRate(),
                contract.getBonusPercent(),
                contract.getWorkHoursPerWeek(),
                contract.isMainJob(),
                active
        );
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }
}
