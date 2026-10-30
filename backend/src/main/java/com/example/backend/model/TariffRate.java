package com.example.backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tariff_rates")
public class TariffRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal baseRate;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private String legalAct;
}
