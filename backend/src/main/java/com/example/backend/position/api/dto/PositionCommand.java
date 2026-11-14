package com.example.backend.position.api.dto;

import java.math.BigDecimal;

public record PositionCommand(String name, Integer tariffCategory, BigDecimal tariffCoefficient,
                              String qualificationRequirements) {
}
