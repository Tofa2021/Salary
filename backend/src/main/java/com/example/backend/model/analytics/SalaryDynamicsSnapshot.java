package com.example.backend.model.analytics;

import com.example.backend.model.employee.Department;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "salary_dynamics_snapshots")
public class SalaryDynamicsSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer year;
    private Integer month;

    @ManyToOne
    private Department department;

    private BigDecimal averageSalary;
    private BigDecimal medianSalary;
    private BigDecimal totalPayrollFund;
    private BigDecimal totalNetPay;
    private BigDecimal totalTaxes;
    private BigDecimal totalFsznEmployer;
    private BigDecimal totalFsznEmployee;
    private BigDecimal totalBelgosstrakh;
    private Integer employeeCount;
    private BigDecimal averageSalaryGrowthPercent;
}
