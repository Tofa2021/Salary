package com.example.backend.employee.domain.model;

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
}
