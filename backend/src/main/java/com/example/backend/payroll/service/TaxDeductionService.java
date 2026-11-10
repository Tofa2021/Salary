package com.example.backend.payroll.service;

import com.example.backend.employee.domain.exception.employee.EmployeeNotFoundException;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.infrastructure.repository.EmployeeRepository;
import com.example.backend.payroll.exception.deduction.DeductionNotFoundException;
import com.example.backend.payroll.exception.deduction.DuplicateChildDeductionException;
import com.example.backend.payroll.exception.deduction.InvalidDeductionPeriodException;
import com.example.backend.payroll.exception.deduction.UnknownDeductionTypeException;
import com.example.backend.payroll.model.DeductionType;
import com.example.backend.payroll.model.TaxDeduction;
import com.example.backend.payroll.repository.TaxDeductionRepository;
import com.example.backend.shared.dto.deduction.CreateDeductionRequest;
import com.example.backend.shared.dto.deduction.DeductionResult;
import com.example.backend.shared.dto.deduction.UpdateDeductionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxDeductionService {
    private final TaxDeductionRepository deductionRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public DeductionResult create(Long employeeId, CreateDeductionRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        DeductionType type = parseDeductionType(request.type());

        if (request.validTo() != null && request.validTo().isBefore(request.validFrom())) {
            throw new InvalidDeductionPeriodException(request.validFrom(), request.validTo());
        }

        if (type == DeductionType.CHILD && request.documentNumber() != null) {
            boolean duplicate = deductionRepository.existsByEmployeeIdAndTypeAndDocumentNumber(
                    employeeId, DeductionType.CHILD, request.documentNumber());
            if (duplicate) {
                throw new DuplicateChildDeductionException(employeeId, request.documentNumber());
            }
        }

        TaxDeduction deduction = new TaxDeduction(
                employee,
                type,
                request.amount(),
                request.validFrom(),
                request.validTo(),
                request.documentNumber()
        );

        return DeductionResult.from(deductionRepository.save(deduction), LocalDate.now());
    }

    @Transactional(readOnly = true)
    public DeductionResult getById(Long id) {
        return DeductionResult.from(findById(id), LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<DeductionResult> findByEmployee(Long employeeId) {
        LocalDate today = LocalDate.now();
        return deductionRepository.findByEmployeeId(employeeId).stream()
                .map(d -> DeductionResult.from(d, today))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DeductionResult> findActiveOnDate(Long employeeId, LocalDate date) {
        LocalDate on = date != null ? date : LocalDate.now();
        return deductionRepository.findActiveOnDate(employeeId, on).stream()
                .map(d -> DeductionResult.from(d, on))
                .toList();
    }

    @Transactional
    public DeductionResult update(Long id, UpdateDeductionRequest request) {
        TaxDeduction deduction = findById(id);
        if (request.amount() != null) {
            deduction.setAmount(request.amount());
        }

        if (request.documentNumber() != null) {
            deduction.setDocumentNumber(request.documentNumber());
        }

        if (request.validTo() != null) {
            if (request.validTo().isBefore(deduction.getValidFrom())) {
                throw new InvalidDeductionPeriodException(
                        deduction.getValidFrom(), request.validTo());
            }
            deduction.setValidTo(request.validTo());
        }

        return DeductionResult.from(deduction, LocalDate.now());
    }

    @Transactional
    public DeductionResult close(Long id) {
        TaxDeduction deduction = findById(id);
        deduction.setValidTo(LocalDate.now());
        return DeductionResult.from(deduction, LocalDate.now());
    }

    @Transactional
    public void delete(Long id) {
        deductionRepository.delete(findById(id));
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateTotalDeductions(
            Long employeeId,
            LocalDate date,
            BigDecimal taxableIncome,
            BigDecimal standardSelfIncomeLimit
    ) {
        return deductionRepository.findActiveOnDate(employeeId, date).stream()
                .filter(d -> isApplicable(d, taxableIncome, standardSelfIncomeLimit))
                .map(TaxDeduction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isApplicable(TaxDeduction d, BigDecimal income, BigDecimal limit) {
        if (d.getType() == DeductionType.STANDARD_SELF) {
            return limit == null || income.compareTo(limit) <= 0;
        }

        return true;
    }

    private DeductionType parseDeductionType(String value) {
        if (value == null) {
            throw new UnknownDeductionTypeException(null);
        }

        try {
            return DeductionType.valueOf(value);
        } catch (IllegalArgumentException ex) {
            throw new UnknownDeductionTypeException(value);
        }
    }

    private TaxDeduction findById(Long id) {
        return deductionRepository.findById(id)
                .orElseThrow(() -> new DeductionNotFoundException(id));
    }
}
