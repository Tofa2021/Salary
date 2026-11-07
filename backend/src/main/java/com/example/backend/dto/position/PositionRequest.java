package com.example.backend.dto.position;

import java.math.BigDecimal;

public record PositionRequest(String name, Integer tariffCategory, BigDecimal tariffCoefficient,
                              String qualificationRequirements) {
}
