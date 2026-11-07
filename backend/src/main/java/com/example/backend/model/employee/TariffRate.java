package com.example.backend.model.employee;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "tariff_rates")
public class TariffRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal baseRate;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String legalAct;

    public TariffRate(BigDecimal baseRate, LocalDate effectiveFrom, LocalDate effectiveTo, String legalAct) {
        this.baseRate = baseRate;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.legalAct = legalAct;
    }
}
