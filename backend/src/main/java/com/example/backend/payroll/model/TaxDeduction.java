package com.example.backend.payroll.model;

import com.example.backend.employee.domain.model.Employee;
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
@AllArgsConstructor
@NoArgsConstructor
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

    public TaxDeduction(
            Employee employee,
            DeductionType type,
            BigDecimal amount,
            LocalDate validFrom,
            LocalDate validTo,
            String documentNumber
    ) {
        this.employee = employee;
        this.type = type;
        this.amount = amount;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.documentNumber = documentNumber;
    }
}
