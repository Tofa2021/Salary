package com.example.backend.employee.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employment_contracts")
public class EmploymentContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employee employee;

    private String contractNumber;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    private BigDecimal baseSalary;
    private BigDecimal hourlyRate;
    private BigDecimal bonusPercent;

    private int workHoursPerWeek;

    @Column(name = "is_main_job")
    private boolean mainJob;

    public EmploymentContract(
            Employee employee,
            String contractNumber,
            LocalDate startDate,
            LocalDate endDate,
            ContractType contractType,
            BigDecimal baseSalary,
            BigDecimal hourlyRate,
            BigDecimal bonusPercent,
            int workHoursPerWeek,
            boolean mainJob
    ) {
        this.employee = employee;
        this.contractNumber = contractNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contractType = contractType;
        this.baseSalary = baseSalary;
        this.hourlyRate = hourlyRate;
        this.bonusPercent = bonusPercent;
        this.workHoursPerWeek = workHoursPerWeek;
        this.mainJob = mainJob;
    }
}
