package com.example.backend.position.api.dto;

import java.math.BigDecimal;

public record PositionResult(Long id, String name, int tariffCategory, BigDecimal tariffCoefficient,
                             String qualificationRequirements) {
}
