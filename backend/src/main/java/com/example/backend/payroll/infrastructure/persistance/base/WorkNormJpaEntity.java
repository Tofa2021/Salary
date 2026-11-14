package com.example.backend.payroll.infrastructure.persistance.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "work_norms",
        uniqueConstraints = @UniqueConstraint(name = "uk_work_norms_year_month",
                columnNames = {"year", "month"}))
public class WorkNormJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month;

    @Column(name = "norm_hours", nullable = false, precision = 10, scale = 2)
    private BigDecimal normHours;
}
