package com.example.backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "payroll_records", uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "year", "month"}))
public class PayrollRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;

    private Integer year;
    private Integer month;

    private BigDecimal salaryAmount;
    private BigDecimal bonusAmount;
    private BigDecimal allowancesAmount;
    private BigDecimal overtimeAmount;
    private BigDecimal nightWorkAmount;
    private BigDecimal vacationPay;
    private BigDecimal sickLeavePay;
    private BigDecimal otherAccruals;

    private BigDecimal totalAccrued;

    private BigDecimal incomeTax;
    private BigDecimal pensionContribution;

    private BigDecimal totalDeducted;

    private BigDecimal netPay;

    private int workedDays;
    private int workedHours;

    @Enumerated(EnumType.STRING)
    private PayrollStatus status;

    private LocalDateTime calculatedAt;

    @OneToMany(mappedBy = "payrollRecord")
    private List<PayrollDetail> details;
}
