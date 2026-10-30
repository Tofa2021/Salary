package com.example.backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payroll_details")
public class PayrollDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private PayrollRecord payrollRecord;

    @ManyToOne
    private PayrollType payrollType;

    private BigDecimal amount;
    private BigDecimal baseAmount;
    private BigDecimal rate;

    private String comment;
}
