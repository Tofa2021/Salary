package com.example.backend.employee.application.service;

import com.example.backend.employee.api.dto.CreateEmploymentContractCommand;
import com.example.backend.employee.api.dto.EmploymentContractResult;
import com.example.backend.employee.api.dto.UpdateEmploymentContractCommand;
import com.example.backend.employee.domain.exception.employee.EmployeeNotFoundException;
import com.example.backend.employee.domain.exception.employmentcontract.EmploymentContractNotFoundException;
import com.example.backend.employee.domain.exception.employmentcontract.EmploymentContractNumberAlreadyExistsException;
import com.example.backend.employee.domain.model.ContractType;
import com.example.backend.employee.domain.model.EmploymentContract;
import com.example.backend.employee.domain.policy.MainJobPolicy;
import com.example.backend.employee.domain.port.EmployeeRepository;
import com.example.backend.employee.domain.port.EmploymentContractRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class EmploymentContractService {
    private final EmploymentContractRepository employmentContractRepository;
    private final EmployeeRepository employeeRepository;
    private final MainJobPolicy mainJobPolicy;
    private final Clock clock;

    public EmploymentContractResult create(Long employeeId, CreateEmploymentContractCommand command) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeNotFoundException(employeeId);
        }

        if (employmentContractRepository.existsByContractNumber(command.contractNumber())) {
            throw new EmploymentContractNumberAlreadyExistsException(command.contractNumber());
        }

        if (command.mainJob()) {
            mainJobPolicy.ensureNoOverlappingMainJob(
                    true,
                    employmentContractRepository.findOverlappingMainJob(
                            employeeId, null, command.startDate(), command.endDate())
            );
        }

        EmploymentContract contract = EmploymentContract.create(
                employeeId, command.contractNumber(), command.startDate(), command.endDate(),
                ContractType.from(command.contractType()), command.baseSalary(),
                command.hourlyRate(), command.bonusPercent(), command.workHoursPerWeek(), command.mainJob()
        );

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

    public EmploymentContractResult update(Long id, UpdateEmploymentContractCommand command) {
        EmploymentContract contract = findById(id);
        BigDecimal base = command.baseSalary() != null ? command.baseSalary() : contract.getBaseSalary();
        BigDecimal hourly = command.hourlyRate() != null ? command.hourlyRate() : contract.getHourlyRate();
        BigDecimal bonus = command.bonusPercent() != null ? command.bonusPercent() : contract.getBonusPercent();
        contract.changeSalary(base, hourly, bonus);

        if (command.workHoursPerWeek() != null) {
            contract.changeWorkHours(command.workHoursPerWeek());
        }

        employmentContractRepository.save(contract);
        return toResult(contract, today());
    }

    public EmploymentContractResult terminate(Long id, LocalDate terminationDate) {
        EmploymentContract contract = findById(id);
        contract.terminate(terminationDate);
        employmentContractRepository.save(contract);
        return toResult(contract, today());
    }

    public EmploymentContractResult extend(Long id, LocalDate newEndDate) {
        EmploymentContract contract = findById(id);
        contract.extend(newEndDate);
        employmentContractRepository.save(contract);
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

    private EmploymentContract findById(Long id) {
        return employmentContractRepository.findById(id)
                .orElseThrow(() -> new EmploymentContractNotFoundException(id));
    }

    private EmploymentContractResult toResult(EmploymentContract contract, LocalDate today) {
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
                contract.isActiveOnDate(today)
        );
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }
}
