package com.example.backend.payroll.application.service;

import com.example.backend.employee.api.EmployeeLookup;
import com.example.backend.payroll.api.dto.deduction.ChangeDeductionPeriodCommand;
import com.example.backend.payroll.api.dto.deduction.ChangeDeductionValueCommand;
import com.example.backend.payroll.api.dto.deduction.CreateDeductionCommand;
import com.example.backend.payroll.api.dto.deduction.DeductionResultDto;
import com.example.backend.payroll.domain.deduction.exception.DeductionNotFoundException;
import com.example.backend.payroll.domain.deduction.exception.InvalidDeductionException;
import com.example.backend.payroll.domain.deduction.model.Deduction;
import com.example.backend.payroll.domain.deduction.port.DeductionRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class DeductionService {
    private final DeductionRepository deductionRepository;
    private final EmployeeLookup employeeLookup;

    public DeductionResultDto create(CreateDeductionCommand command) {
        employeeLookup.findById(command.employeeId())
                .orElseThrow(() -> new InvalidDeductionException("employee not found: " + command.employeeId()));
        Deduction deduction = Deduction.create(
                command.employeeId(), command.type(), command.description(),
                command.percentage(), command.fixedAmount(), command.base(), command.validPeriod());
        return toResult(deductionRepository.save(deduction));
    }

    public DeductionResultDto getById(Long id) {
        return toResult(findById(id));
    }

    public List<DeductionResultDto> getByEmployee(Long employeeId, LocalDate date) {
        return deductionRepository.findActiveByEmployeeOnDate(employeeId, date).stream()
                .map(this::toResult).toList();
    }

    public DeductionResultDto changeValue(Long id, ChangeDeductionValueCommand command) {
        Deduction deduction = findById(id);

        deduction.changeValue(command.type(), command.description(), command.percentage(), command.fixedAmount(), command.base());
        return toResult(deductionRepository.save(deduction));
    }

    public DeductionResultDto changePeriod(Long id, ChangeDeductionPeriodCommand command) {
        Deduction deduction = findById(id);

        deduction.changePeriod(command.validPeriod());
        return toResult(deductionRepository.save(deduction));
    }

    public void delete(Long id) {
        deductionRepository.delete(findById(id));
    }

    private Deduction findById(Long id) {
        return deductionRepository.findById(id)
                .orElseThrow(() -> new DeductionNotFoundException(id));
    }

    private DeductionResultDto toResult(Deduction deduction) {
        return new DeductionResultDto(
                deduction.getId(),
                deduction.getEmployeeId(),
                deduction.getType(),
                deduction.getDescription(),
                deduction.getPercentage(),
                deduction.getFixedAmount(),
                deduction.getBase(),
                deduction.getValidPeriod()
        );
    }
}
