package com.example.backend.payroll.infrastructure.persistance.deduction;

import com.example.backend.payroll.domain.deduction.model.DeductionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "deductions",
        indexes = @Index(name = "idx_deduction_employee_date", columnList = "employee_id, valid_from"))
public class DeductionJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "deduction_type", nullable = false, length = 30)
    private DeductionType type;

    @Column(length = 255)
    private String description;

    @Column(precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "fixed_amount", precision = 19, scale = 2)
    private BigDecimal fixedAmount;

    @Column(name = "base", length = 20)
    private String base;

    @Column(length = 3)
    private String currency;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDate validTo;
}
