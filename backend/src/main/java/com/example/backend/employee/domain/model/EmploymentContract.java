package com.example.backend.employee.domain.model;

import com.example.backend.employee.domain.exception.employmentcontract.EmploymentContractAlreadyTerminatedException;
import com.example.backend.employee.domain.exception.employmentcontract.ExtendEmploymentContractException;
import com.example.backend.employee.domain.exception.employmentcontract.InvalidEmploymentContractPeriodException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
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

    public boolean isActiveOnDate(LocalDate date) {
        return !startDate.isAfter(date) && (endDate == null || endDate.isBefore(date));
    }
}
