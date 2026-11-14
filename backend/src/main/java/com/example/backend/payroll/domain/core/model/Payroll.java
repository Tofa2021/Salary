package com.example.backend.payroll.domain.core.model;

import com.example.backend.payroll.domain.core.exception.InvalidPayrollStatusException;
import com.example.backend.payroll.domain.core.exception.PayrollAlreadyApprovedException;
import com.example.backend.payroll.domain.core.exception.PayrollCalculationException;
import com.example.backend.payroll.domain.deduction.model.DeductionResult;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Period;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Payroll {
    private Long id;
    private Long employeeId;
    private Period period;
    private PayrollStatus status;

    private Money base;
    private Money gross;
    private Money tax;
    private Money socialEmployee;
    private List<DeductionResult> deductions;
    private Money net;
    private Money employerContributions;
    private Money employerCost;

    private LocalDateTime calculatedAt;
    private LocalDateTime approvedAt;
    private Long approvedBy;

    private Payroll() {
    }

    public static Payroll create(Long employeeId, Period period) {
        if (employeeId == null) {
            throw new IllegalArgumentException("employeeId is required");
        }
        if (period == null) {
            throw new IllegalArgumentException("period is required");
        }
        Payroll payroll = new Payroll();
        payroll.employeeId = employeeId;
        payroll.period = period;
        payroll.status = PayrollStatus.DRAFT;
        payroll.deductions = new ArrayList<>();
        return payroll;
    }

    public static Payroll rehydrate(
            Long id, Long employeeId, Period period, PayrollStatus status,
            Money base, Money gross, Money tax, Money socialEmployee,
            List<DeductionResult> deductions, Money net,
            Money employerContributions, Money employerCost,
            LocalDateTime calculatedAt, LocalDateTime approvedAt, Long approvedBy
    ) {
        Payroll payroll = new Payroll();
        payroll.id = id;
        payroll.employeeId = employeeId;
        payroll.period = period;
        payroll.status = status;
        payroll.base = base;
        payroll.gross = gross;
        payroll.tax = tax;
        payroll.socialEmployee = socialEmployee;
        payroll.deductions = new ArrayList<>(deductions);
        payroll.net = net;
        payroll.employerContributions = employerContributions;
        payroll.employerCost = employerCost;
        payroll.calculatedAt = calculatedAt;
        payroll.approvedAt = approvedAt;
        payroll.approvedBy = approvedBy;
        return payroll;
    }

    public void applyCalculation(
            Money base, Money gross, Money tax, Money socialEmployee,
            List<DeductionResult> deductions, Money net,
            Money employerContributions, Money employerCost,
            LocalDateTime calculatedAt
    ) {
        ensureEditable();
        if (gross == null || tax == null || socialEmployee == null || net == null) {
            throw new IllegalArgumentException("calculation amounts are required");
        }
        if (calculatedAt == null) {
            throw new IllegalArgumentException("calculatedAt is required");
        }
        if (net.isGreaterThan(gross)) {
            throw new PayrollCalculationException("net cannot exceed gross");
        }
        if (net.isNegative()) {
            throw new PayrollCalculationException("net cannot be negative");
        }

        this.base = base;
        this.gross = gross;
        this.tax = tax;
        this.socialEmployee = socialEmployee;
        this.deductions = new ArrayList<>(deductions);
        this.net = net;
        this.employerContributions = employerContributions;
        this.employerCost = employerCost;
        this.calculatedAt = calculatedAt;
        this.status = PayrollStatus.CALCULATED;
    }

    public void approve(Long approvedBy, LocalDateTime approvedAt) {
        if (status != PayrollStatus.CALCULATED) {
            throw new InvalidPayrollStatusException(id, status, PayrollStatus.APPROVED);
        }
        if (approvedBy == null || approvedAt == null) {
            throw new IllegalArgumentException("approvedBy and approvedAt are required");
        }
        this.status = PayrollStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
    }

    public void cancel() {
        if (status == PayrollStatus.CANCELLED) {
            throw new InvalidPayrollStatusException(id, status, PayrollStatus.CANCELLED);
        }
        if (status == PayrollStatus.APPROVED) {
            throw new PayrollAlreadyApprovedException(id);
        }
        this.status = PayrollStatus.CANCELLED;
    }

    private void ensureEditable() {
        if (status == PayrollStatus.APPROVED) {
            throw new PayrollAlreadyApprovedException(id);
        }
        if (status == PayrollStatus.CANCELLED) {
            throw new InvalidPayrollStatusException(id, status, PayrollStatus.CALCULATED);
        }
    }
}
