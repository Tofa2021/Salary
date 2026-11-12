package com.example.backend.employee.domain.model;

import com.example.backend.employee.domain.exception.employmentcontract.EmploymentContractAlreadyTerminatedException;
import com.example.backend.employee.domain.exception.employmentcontract.ExtendEmploymentContractException;
import com.example.backend.employee.domain.exception.employmentcontract.InvalidEmploymentContractPeriodException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentContract {
    private Long id;
    private Long employeeId;

    private String contractNumber;
    private LocalDate startDate;
    private LocalDate endDate;

    private ContractType contractType;

    private BigDecimal baseSalary;
    private BigDecimal hourlyRate;
    private BigDecimal bonusPercent;

    private int workHoursPerWeek;

    private boolean mainJob;

    public static EmploymentContract create(
            Long employeeId, String contractNumber,
            LocalDate startDate, LocalDate endDate,
            ContractType contractType, BigDecimal baseSalary,
            BigDecimal hourlyRate, BigDecimal bonusPercent,
            int workHoursPerWeek, boolean mainJob
    ) {
        if (employeeId == null) {
            throw new IllegalArgumentException("employeeId is required");
        }
        if (contractNumber == null || contractNumber.isBlank()) {
            throw new IllegalArgumentException("contractNumber is required");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("startDate is required");
        }
        if (contractType == null) {
            throw new IllegalArgumentException("contractType is required");
        }

        EmploymentContract contract = new EmploymentContract();
        contract.employeeId = employeeId;
        contract.contractNumber = contractNumber;
        contract.startDate = startDate;
        contract.changePeriod(endDate);
        contract.contractType = contractType;
        contract.baseSalary = baseSalary;
        contract.hourlyRate = hourlyRate;
        contract.bonusPercent = bonusPercent;
        contract.workHoursPerWeek = workHoursPerWeek;
        contract.mainJob = mainJob;
        return contract;
    }

    public static EmploymentContract rehydrate(
            Long id, Long employeeId, String contractNumber,
            LocalDate startDate, LocalDate endDate,
            ContractType contractType, BigDecimal baseSalary,
            BigDecimal hourlyRate, BigDecimal bonusPercent,
            int workHoursPerWeek, boolean mainJob
    ) {
        EmploymentContract contract = new EmploymentContract();
        contract.id = id;
        contract.employeeId = employeeId;
        contract.contractNumber = contractNumber;
        contract.startDate = startDate;
        contract.endDate = endDate;
        contract.contractType = contractType;
        contract.baseSalary = baseSalary;
        contract.hourlyRate = hourlyRate;
        contract.bonusPercent = bonusPercent;
        contract.workHoursPerWeek = workHoursPerWeek;
        contract.mainJob = mainJob;
        return contract;
    }

    public void changePeriod(LocalDate newEnd) {
        if (newEnd != null && newEnd.isBefore(startDate)) {
            throw new InvalidEmploymentContractPeriodException(startDate, newEnd);
        }
        this.endDate = newEnd;
    }

    public void terminate(LocalDate date) {
        if (endDate != null && !endDate.isAfter(date)) {
            throw new EmploymentContractAlreadyTerminatedException(id);
        }
        if (date.isBefore(startDate)) {
            throw new InvalidEmploymentContractPeriodException(startDate, date);
        }
        this.endDate = date;
    }

    public void extend(LocalDate newEnd) {
        if (contractType == ContractType.INDEFINITE) {
            throw new ExtendEmploymentContractException("Cannot extend indefinite contract");
        }
        if (newEnd.isBefore(startDate)) {
            throw new InvalidEmploymentContractPeriodException(startDate, newEnd);
        }
        this.endDate = newEnd;
    }

    public void changeSalary(BigDecimal base, BigDecimal hourly, BigDecimal bonus) {
        this.baseSalary = base;
        this.hourlyRate = hourly;
        this.bonusPercent = bonus;
    }

    public void changeWorkHours(int hours) {
        if (hours <= 0) {
            throw new IllegalArgumentException("workHoursPerWeek > 0");
        }
        this.workHoursPerWeek = hours;
    }

    public boolean isActiveOnDate(LocalDate date) {
        return !startDate.isAfter(date) && (endDate == null || !endDate.isBefore(date));
    }
}
