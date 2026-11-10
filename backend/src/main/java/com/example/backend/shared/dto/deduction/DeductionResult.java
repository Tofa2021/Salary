package com.example.backend.shared.dto.deduction;

import com.example.backend.payroll.model.DeductionType;
import com.example.backend.payroll.model.TaxDeduction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DeductionResult(
        Long id,
        Long employeeId,
        DeductionType type,
        BigDecimal amount,
        LocalDate validFrom,
        LocalDate validTo,
        String documentNumber,
        boolean active
) {
    public static DeductionResult from(TaxDeduction deduction, LocalDate today) {
        boolean active = !deduction.getValidFrom().isAfter(today) &&
                (deduction.getValidTo() == null || !deduction.getValidTo().isBefore(today));
        return new DeductionResult(
                deduction.getId(),
                deduction.getEmployee().getId(),
                deduction.getType(),
                deduction.getAmount(),
                deduction.getValidFrom(),
                deduction.getValidTo(),
                deduction.getDocumentNumber(),
                active
        );
    }
}
