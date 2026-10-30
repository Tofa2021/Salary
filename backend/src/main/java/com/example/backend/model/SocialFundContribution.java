package com.example.backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "social_fund_contributions")
public class SocialFundContribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private PayrollRecord payrollRecord;

    @Enumerated(EnumType.STRING)
    private FundType fundType;

    private BigDecimal baseAmount;
    private BigDecimal rate;
    private BigDecimal amount;
}
