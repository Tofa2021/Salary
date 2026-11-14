package com.example.backend.payroll.infrastructure.persistance.core;

import com.example.backend.payroll.domain.core.model.PayrollStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "payrolls",
        uniqueConstraints = @UniqueConstraint(name = "uk_payroll_employee_period",
                columnNames = {"employee_id", "period_year", "period_month"}))
public class PayrollJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "period_year", nullable = false)
    private int periodYear;

    @Column(name = "period_month", nullable = false)
    private int periodMonth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayrollStatus status;

    @Column(precision = 19, scale = 2)
    private BigDecimal baseAmount;

    @Column(length = 3)
    private String baseCurrency;

    @Column(precision = 19, scale = 2)
    private BigDecimal grossAmount;

    @Column(length = 3)
    private String grossCurrency;

    @Column(precision = 19, scale = 2)
    private BigDecimal taxAmount;

    @Column(length = 3)
    private String taxCurrency;

    @Column(precision = 19, scale = 2)
    private BigDecimal socialEmployeeAmount;

    @Column(length = 3)
    private String socialEmployeeCurrency;

    @Column(precision = 19, scale = 2)
    private BigDecimal netAmount;

    @Column(length = 3)
    private String netCurrency;

    @Column(precision = 19, scale = 2)
    private BigDecimal employerContributionsAmount;

    @Column(length = 3)
    private String employerContributionsCurrency;

    @Column(precision = 19, scale = 2)
    private BigDecimal employerCostAmount;

    @Column(length = 3)
    private String employerCostCurrency;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approved_by")
    private Long approvedBy;

    @OneToMany(mappedBy = "payroll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PayrollDeductionJpaEntity> deductions = new ArrayList<>();
}
