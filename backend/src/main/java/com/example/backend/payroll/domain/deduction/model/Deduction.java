package com.example.backend.payroll.domain.deduction.model;

import com.example.backend.payroll.domain.deduction.exception.InvalidDeductionException;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Deduction {
    private Long id;
    private Long employeeId;
    private DeductionType type;
    private String description;
    private Percentage percentage;
    private Money fixedAmount;
    private Base base;
    private DateRange validPeriod;

    private Deduction() {
    }

    public static Deduction create(
            Long employeeId, DeductionType type, String description,
            Percentage percentage, Money fixedAmount, Base base,
            DateRange validPeriod
    ) {
        if (employeeId == null) {
            throw new InvalidDeductionException("employeeId is required");
        }
        if (type == null) {
            throw new InvalidDeductionException("type is required");
        }
        if (validPeriod == null) {
            throw new InvalidDeductionException("validPeriod is required");
        }
        if (percentage == null && fixedAmount == null) {
            throw new InvalidDeductionException("percentage or fixedAmount is required");
        }
        if (percentage != null && fixedAmount != null) {
            throw new InvalidDeductionException("only one of percentage/fixedAmount");
        }
        if (percentage != null && base == null) {
            throw new InvalidDeductionException("base required for percentage");
        }

        Deduction deduction = new Deduction();
        deduction.employeeId = employeeId;
        deduction.type = type;
        deduction.description = description;
        deduction.percentage = percentage;
        deduction.fixedAmount = fixedAmount;
        deduction.base = base;
        deduction.validPeriod = validPeriod;
        return deduction;
    }

    public static Deduction rehydrate(
            Long id, Long employeeId, DeductionType type, String description,
            Percentage percentage, Money fixedAmount, Base base,
            DateRange validPeriod
    ) {
        Deduction deduction = new Deduction();
        deduction.id = id;
        deduction.employeeId = employeeId;
        deduction.type = type;
        deduction.description = description;
        deduction.percentage = percentage;
        deduction.fixedAmount = fixedAmount;
        deduction.base = base;
        deduction.validPeriod = validPeriod;
        return deduction;
    }

    public boolean appliesTo(LocalDate date) {
        return validPeriod.contains(date);
    }

    public void changeValue(DeductionType type, String description, Percentage percentage, Money fixedAmount, Base base) {
        if (type == null) {
            throw new InvalidDeductionException("type is required");
        }
        if (percentage == null && fixedAmount == null) {
            throw new InvalidDeductionException("percentage or fixedAmount is required");
        }
        if (percentage != null && fixedAmount != null) {
            throw new InvalidDeductionException("only one of percentage/fixedAmount");
        }
        if (percentage != null && base == null) {
            throw new InvalidDeductionException("base required for percentage");
        }
        this.type = type;
        this.description = description;
        this.percentage = percentage;
        this.fixedAmount = fixedAmount;
        this.base = base;
    }

    public void changePeriod(DateRange validPeriod) {
        if (validPeriod == null) {
            throw new InvalidDeductionException("validPeriod is required");
        }
        this.validPeriod = validPeriod;
    }
}
