package com.example.backend.position;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "positions")
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int tariffCategory;
    private BigDecimal tariffCoefficient;

    private String qualificationRequirements;

    public Position(String name, int tariffCategory, BigDecimal tariffCoefficient, String qualificationRequirements) {
        this.name = name;
        this.tariffCategory = tariffCategory;
        this.tariffCoefficient = tariffCoefficient;
        this.qualificationRequirements = qualificationRequirements;
    }
}
