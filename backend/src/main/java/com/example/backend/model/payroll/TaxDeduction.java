package com.example.backend.model.payroll;

import com.example.backend.model.employee.Employee;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tax_deductions")
public class TaxDeduction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private DeductionType type;

    private BigDecimal amount;
    private LocalDate validFrom;
    private LocalDate validTo;

    private String documentNumber;
}
