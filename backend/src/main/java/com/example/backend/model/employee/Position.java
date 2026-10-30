package com.example.backend.model.employee;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "positions")
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int tariffCategory;
    private BigDecimal tariffCoefficient;

    private String qualificationRequirements;
}
