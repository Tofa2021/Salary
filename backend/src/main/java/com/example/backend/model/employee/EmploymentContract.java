package com.example.backend.model.employee;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
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
}
