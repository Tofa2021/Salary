package com.example.backend.dto.position;

import com.example.backend.model.employee.Position;

import java.math.BigDecimal;

public record PositionResult(Long id, String name, int tariffCategory, BigDecimal tariffCoefficient,
                             String qualificationRequirements) {
    public static PositionResult fromPosition(Position position) {
        return new PositionResult(
                position.getId(),
                position.getName(),
                position.getTariffCategory(),
                position.getTariffCoefficient(),
                position.getQualificationRequirements()
        );
    }
}
